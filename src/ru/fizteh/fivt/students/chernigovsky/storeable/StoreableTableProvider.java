package ru.fizteh.fivt.students.chernigovsky.storeable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.chernigovsky.junit.AbstractTableProvider;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoreableTableProvider extends AbstractTableProvider<ExtendedStoreableTable> implements ExtendedStoreableTableProvider {
    public StoreableTableProvider(File newDbDirectory, boolean flag) {
        super(newDbDirectory, flag);
        if (newDbDirectory != null) {
            for (String string : newDbDirectory.list()) {
                ExtendedStoreableTable newTable = new StoreableTable(string, flag, null, this);
                tableHashMap.put(string, newTable);
                try {
                    StoreableUtils.readTable(newTable, this);
                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        }
    }

    /**
     * Создаёт таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблица уже существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    public ExtendedStoreableTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("wrong table name");
        }

        try {
            tableProviderLock.readLock().lock();
            if (tableHashMap.get(name) != null) {
                return null;
            }
        } finally {
            tableProviderLock.readLock().unlock();
        }

        if (columnTypes == null) {
            throw new IllegalArgumentException("columnTypes is null");
        }

        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("ColumnTypes list is empty");
        }


        try {
            tableProviderLock.writeLock().lock();
            File tableDirectory = new File(getDbDirectory(), name);
            if (!tableDirectory.mkdir()) {
                throw new IllegalArgumentException("directory making error");
            }

            File signature = new File(tableDirectory, "signature.tsv");
            if (!signature.createNewFile()) {
                throw new IllegalArgumentException("signature making error");
            }

            FileOutputStream fileOutputStream = new FileOutputStream(signature);
            fileOutputStream.getChannel().truncate(0); // Clear file
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            StringBuilder stringBuilder = new StringBuilder();

            for (Class<?> type : columnTypes) {
                if (type == null) {
                    throw new IllegalArgumentException("wrong column type");
                }
                TypeEnum typesEnum = TypeEnum.getByClass(type);
                if (typesEnum == null) {
                    throw new IllegalArgumentException("wrong column type");
                }
                stringBuilder.append(TypeEnum.getByClass(type).getSignature());
                stringBuilder.append(' ');
            }

            String typeString = stringBuilder.toString();

            typeString = typeString.substring(0, typeString.length() - 1);

            try {
                dataOutputStream.write(typeString.getBytes("UTF-8"));
            } finally {
                dataOutputStream.close();
            }

            ExtendedStoreableTable newTable = new StoreableTable(name, autoCommit, columnTypes, this);

            tableHashMap.put(name, newTable);

            return newTable;
        } finally {
            tableProviderLock.writeLock().unlock();
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
    public MyStoreable deserialize(Table table, String value) throws ParseException {
        if (table == null) {
            throw new IllegalArgumentException();
        }

        if (value == null) {
            return null;
        }

        JSONArray array;
        try {
            array = new JSONArray(value);
        } catch (JSONException ex) {
            throw new ParseException("JSONArray create error", -1);
        }

        if (array.length() != table.getColumnsCount()) {
            throw new ParseException("incorrect value", -1);
        }

        List<Object> deserializedValue = new ArrayList<Object>();

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (array.get(i).equals(null)) {
                deserializedValue.add(null);
            } else if (array.get(i).getClass() == Integer.class && table.getColumnType(i) == Integer.class) {
                deserializedValue.add(array.getInt(i));
            } else if ((array.get(i).getClass() == Long.class || array.get(i).getClass() == Integer.class) &&
                    table.getColumnType(i) == Long.class) {
                deserializedValue.add(array.getLong(i));
            } else if (array.get(i).getClass() == Integer.class && table.getColumnType(i) == Byte.class) {
                Integer a = array.getInt(i);
                deserializedValue.add(a.byteValue());
            } else if (array.get(i).getClass() == Double.class && table.getColumnType(i) == Float.class) {
                Double a = array.getDouble(i);
                deserializedValue.add(a.floatValue());
            } else if (array.get(i).getClass() == Double.class && table.getColumnType(i) == Double.class) {
                deserializedValue.add(array.getDouble(i));
            } else if (array.get(i).getClass() == Boolean.class && table.getColumnType(i) == Boolean.class) {
                deserializedValue.add(array.getBoolean(i));
            } else if (array.get(i).getClass() == String.class && table.getColumnType(i) == String.class) {
                deserializedValue.add(array.getString(i));
            } else {
                throw new ParseException("Incorrect value string.", -1);
            }
        }

        return createFor(table, deserializedValue);
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
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        Object[] serializedValue = new Object[table.getColumnsCount()];

        StoreableUtils.checkValue(table, value);

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            serializedValue[i] = value.getColumnAt(i);
        }
        JSONArray array = new JSONArray(serializedValue);
        return array.toString();
    }

    /**
     * Создает новый пустой {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Пустой {@link ru.fizteh.fivt.storage.structured.Storeable}, нацеленный на использование с этой таблицей.
     */
    public MyStoreable createFor(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("null table");
        }
        return new MyStoreable(table);
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
    public MyStoreable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table == null || values == null || values.isEmpty()) {
            throw new IllegalArgumentException("null value or table");
        }

        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("invalid values count");
        }

        MyStoreable storeable = new MyStoreable(table);
        for (int i = 0; i < values.size(); ++i) {
            storeable.setColumnAt(i, values.get(i));
        }

        return storeable;
    }
}
