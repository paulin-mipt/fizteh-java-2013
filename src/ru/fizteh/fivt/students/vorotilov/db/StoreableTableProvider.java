package ru.fizteh.fivt.students.vorotilov.db;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.vorotilov.shell.FileUtil;
import ru.fizteh.fivt.students.vorotilov.shell.FileWasNotDeleted;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Управляющий класс для работы с {@link ru.fizteh.fivt.storage.structured.Table таблицами}
 *
 * Предполагает, что актуальная версия с устройства хранения, сохраняется при создании
 * экземпляра объекта. Далее ввод-вывод выполняется только в момент создания и удаления
 * таблиц.
 *
 * Данный интерфейс не является потокобезопасным.
 */
public class StoreableTableProvider implements TableProvider {

    private final File rootDir;

    private HashMap<String, StoreableTable> tables;
    private ReadWriteLock providerLock = new ReentrantReadWriteLock(true);

    StoreableTableProvider(File rootDir) {
        if (rootDir == null) {
            throw new IllegalArgumentException("Main root dir is null");
        } else if (!rootDir.exists()) {
            throw new IllegalArgumentException("Proposed root dir not exists");
        } else if (!rootDir.isDirectory()) {
            throw new IllegalArgumentException("Proposed object is not directory");
        }
        this.rootDir = rootDir;
        tables = new HashMap<>();
    }

    /**
     * Возвращает таблицу с указанным названием.
     *
     * Последовательные вызовы метода с одинаковыми аргументами должны возвращать один и тот же объект таблицы,
     * если он не был удален с помощью {@link #removeTable(String)}.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     *
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    @Override
    public StoreableTable getTable(String name) {
        checkTableName(name);
        providerLock.writeLock().lock();
        try {
            StoreableTable requestedTable = tables.get(name);
            if (requestedTable != null) {
                return requestedTable;
            } else {
                File tableRootDir = new File(rootDir, name);
                if (!tableRootDir.exists()) {
                    return null;
                } else {
                    StoreableTable newOpenedTable = new StoreableTable(this, tableRootDir);
                    tables.put(name, newOpenedTable);
                    return new StoreableTable(this, tableRootDir);
                }
            }
        } finally {
            providerLock.writeLock().unlock();
        }

    }

    /**
     * Создаёт таблицу с указанным названием.
     * Создает новую таблицу. Совершает необходимые дисковые операции.
     *
     * @param name Название таблицы.
     * @param columnTypes Типы колонок таблицы. Не может быть пустой.
     * @return Объект, представляющий таблицу. Если таблица с указанным именем существует, возвращает null.
     *
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение. Если список типов
     *                                  колонок null или содержит недопустимые значения.
     * @throws java.io.IOException При ошибках ввода/вывода.
     */
    @Override
    public StoreableTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        checkTableName(name);
        if (columnTypes == null) {
            throw new IllegalArgumentException("Column type is null");
        }
        if (columnTypes.size() == 0) {
            throw new IllegalArgumentException("Can't create table without colums");
        }
        File tableRootDir = new File(rootDir, name);
        providerLock.writeLock().lock();
        try {
            if (tableRootDir.exists()) {
                return null;
            } else {
                if (!tableRootDir.mkdir()) {
                    throw new IllegalStateException("Can't make table root dir");
                }
                StoreableTable newTable = new StoreableTable(this, tableRootDir, columnTypes);
                tables.put(name, newTable);
                return newTable;
            }
        } finally {
            providerLock.writeLock().unlock();
        }
    }

    /**
     * Удаляет существующую таблицу с указанным названием.
     *
     * Объект удаленной таблицы, если был кем-то взят с помощью {@link #getTable(String)},
     * с этого момента должен бросать {@link IllegalStateException}.
     *
     * @param name Название таблицы.
     *
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException Если таблицы с указанным названием не существует.
     * @throws java.io.IOException - при ошибках ввода/вывода.
     */
    @Override
    public void removeTable(String name) throws IOException {
        checkTableName(name);
        providerLock.writeLock().lock();
        try {
            tables.remove(name);
            File tableRootDir = new File(rootDir, name);
            if (!tableRootDir.exists()) {
                throw new IllegalStateException("No table with this name");
            } else {
                try {
                    FileUtil.recursiveDelete(rootDir, tableRootDir);
                } catch (FileWasNotDeleted e) {
                    throw new IllegalStateException("Can't delete table");
                }
            }
        } finally {
            providerLock.writeLock().unlock();
        }
    }

    /**
     * Преобразовывает строку в объект {@link ru.fizteh.fivt.storage.structured.Storeable}, соответствующий структуре таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value Строка, из которой нужно прочитать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Прочитанный {@link ru.fizteh.fivt.storage.structured.Storeable}.
     *
     * @throws java.text.ParseException - при каких-либо несоответстиях в прочитанных данных.
     */
    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException("Can't parse json array", 0);
        }
        if (jsonArray == null) {
            throw new ParseException("Can't parse json array", 0);
        }
        if (jsonArray.length() != table.getColumnsCount()) {
            throw new ParseException("Different length of value and table types", 0);
        }
        Storeable tableRow = createFor(table);
        for (int i = 0; i < jsonArray.length(); ++i) {
            if (jsonArray.get(i).equals(null)) {
                tableRow.setColumnAt(i, null);
            } else if (table.getColumnType(i).equals(jsonArray.get(i).getClass())) {
                tableRow.setColumnAt(i, jsonArray.get(i));
            } else if (table.getColumnType(i).equals(Long.class)
                    && (jsonArray.get(i).getClass().equals(Long.class)
                        || jsonArray.get(i).getClass().equals(Integer.class))) {
                tableRow.setColumnAt(i, jsonArray.getLong(i));
            } else if (table.getColumnType(i).equals(Byte.class)
                    && jsonArray.get(i).getClass().equals(Integer.class)) {
                tableRow.setColumnAt(i, (new Integer(jsonArray.getInt(i))).byteValue());
            } else if (table.getColumnType(i).equals(Float.class)
                    && jsonArray.get(i).getClass().equals(Double.class)) {
                tableRow.setColumnAt(i, (new Double(jsonArray.getDouble(i)).floatValue()));
            } else {
                throw new ParseException("Unknown type " + jsonArray.get(i).getClass()
                        + " ; expected: " + table.getColumnType(i), 0);
            }
        }
        return tableRow;
    }

    /**
     * Преобразовывает объект {@link ru.fizteh.fivt.storage.structured.Storeable} в строку.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value {@link ru.fizteh.fivt.storage.structured.Storeable}, который нужно записать.
     * @return Строка с записанным значением.
     *
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа в {@link ru.fizteh.fivt.storage.structured.Storeable} и типа колонки в таблице.
     */
    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        JSONArray jsonArray = new JSONArray();
        checkTableRow(table, (TableRow) value);
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i) != null && !table.getColumnType(i).equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException("Type mismatch");
            }
            jsonArray.put(value.getColumnAt(i));
        }
        return jsonArray.toString();
    }

    /**
     * Создает новый пустой {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Пустой {@link ru.fizteh.fivt.storage.structured.Storeable}, нацеленный на использование с этой таблицей.
     */
    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> columnTypes = new ArrayList<>(table.getColumnsCount());
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columnTypes.add(i, table.getColumnType(i));
        }
        return new TableRow(columnTypes);
    }

    /**
     * Создает новый {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы, подставляя туда переданные значения.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param values Список значений, которыми нужно проинициализировать поля Storeable.
     * @return {@link ru.fizteh.fivt.storage.structured.Storeable}, проинициализированный переданными значениями.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа переданного значения и колонки.
     * @throws IndexOutOfBoundsException При несоответствии числа переданных значений и числа колонок.
     */
    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Class<?>> columnTypes = new ArrayList<>(table.getColumnsCount());
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columnTypes.add(i, table.getColumnType(i));
        }
        TableRow tableRow = new TableRow(columnTypes);
        if (values.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("Diffent size of values and table storeable");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            tableRow.setColumnAt(i, values.get(i));
        }
        return tableRow;
    }

    /**
     * Проверяет имя таблицы на корректность
     *
     * @param name Название таблицы.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    private void checkTableName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name is empty");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new IllegalArgumentException("Bad symbols in table name");
        }
    }

    public File getRoot() {
        return rootDir;
    }

    private void checkTableRow(Table table, TableRow value) {
        if (table.getColumnsCount() != value.getColumsCount()) {
            throw new ColumnFormatException("Wrong number of colums to serialize");
        }
    }

}
