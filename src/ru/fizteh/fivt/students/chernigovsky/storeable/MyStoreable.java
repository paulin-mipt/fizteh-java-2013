package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;

/**
 * Список фиксированной структуры, строка таблицы {@link ru.fizteh.fivt.storage.structured.Table}.
 *
 * Нумерация колонок с нуля. Позиция в списке соответствует колонке таблицы под тем же номером.
 *
 * С помощью {@link ru.fizteh.fivt.storage.structured.TableProvider} может быть сериализован или десериализован.
 *
 * Для получения объекта из нужной колонки воспользуйтесь соответствующим геттером.
 * Для установки объекта а колонку воспользуйтесь {@link #setColumnAt(int, Object)} .
 */
public class MyStoreable implements Storeable {
    private ArrayList<Object> columns;
    private Table table;

    public MyStoreable(Table newTable) {
        if (newTable == null) {
            throw new IllegalArgumentException("table is null");
        }
        table = newTable;
        columns = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columns.add(null);
        }

    }

    /**
     * Установить значение в колонку
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @param value - значение, которое нужно установить.
     *              Может быть null.
     *              Тип значения должен соответствовать декларированному типу колонки.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Тип значения не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (value != null && !table.getColumnType(columnIndex).equals(value.getClass())) {
            throw new ColumnFormatException();
        }
        columns.set(columnIndex, value);
    }

    /**
     * Возвращает значение из данной колонки, не приводя его к конкретному типу.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, без приведения типа. Может быть null.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException();
        }
        return columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Integer.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Integer. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!table.getColumnType(columnIndex).equals(Integer.class)) {
            throw new ColumnFormatException();
        }
        return (Integer) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Long.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Long. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!table.getColumnType(columnIndex).equals(Long.class)) {
            throw new ColumnFormatException();
        }
        return (Long) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Byte.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Byte. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!table.getColumnType(columnIndex).equals(Byte.class)) {
            throw new ColumnFormatException();
        }
        return (Byte) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Float.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Float. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!table.getColumnType(columnIndex).equals(Float.class)) {
            throw new ColumnFormatException();
        }
        return (Float) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Double.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Double. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!table.getColumnType(columnIndex).equals(Double.class)) {
            throw new ColumnFormatException();
        }
        return (Double) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Boolean.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Boolean. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!table.getColumnType(columnIndex).equals(Boolean.class)) {
            throw new ColumnFormatException();
        }
        return (Boolean) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к String.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к String. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!table.getColumnType(columnIndex).equals(String.class)) {
            throw new ColumnFormatException();
        }
        return (String) columns.get(columnIndex);
    }
}

