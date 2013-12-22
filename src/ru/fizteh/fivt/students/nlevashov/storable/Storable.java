package ru.fizteh.fivt.students.nlevashov.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class Storable implements Storeable {

    List<Class<?>> types;
    List<Object> values;

    /**
     * Конструктор без значений
     * @param columnTypes - Список типов
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Попытка вставить неразрешенный тип.
     */
    public Storable(List<Class<?>> columnTypes) throws ColumnFormatException {
        if ((columnTypes == null) || (columnTypes.isEmpty())) {
            throw new ColumnFormatException("Storable.constructor: columnTypes is null");
        }
        for (Class<?> c : columnTypes)  {
            if ((c != Integer.class) && (c != Long.class) && (c != Byte.class) && (c != Float.class)
                                     && (c != Double.class) && (c != Boolean.class) && (c != String.class)) {
                throw new ColumnFormatException("Storable.constructor: Illegal type \"" + c.toString() + "\"");
            }
        }
        types = columnTypes;
        values = new ArrayList<>(Collections.nCopies(types.size(), null));
    }

    /**
     * Конструктор без значений
     * @param columnTypes - Список типов
     * @param columnValues - Список значений
     * Типы значений должны соответствовать типам колонки.
     *
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Попытка вставить неразрешенный тип или
     *                         тип хотя бы одного из значений не соответствует типу соответствующей ему колонки.
     */
    public Storable(List<Class<?>> columnTypes, List<Object> columnValues)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if ((columnTypes == null) || (columnTypes.isEmpty())) {
            throw new ColumnFormatException("Storable.constructor: columnTypes is null");
        }
        if ((columnValues == null) || (columnValues.isEmpty())) {
            throw new ColumnFormatException("Storable.constructor: columnTypes is null");
        }
        if (columnTypes.size() != columnValues.size()) {
            throw new IndexOutOfBoundsException("Storable.constructor: "
                                              + "columnTypes and columnValues have different size");
        }
        int i = 0;
        for (Class<?> c : columnTypes)  {
            if ((c != Integer.class) && (c != Long.class) && (c != Byte.class) && (c != Float.class)
                                     && (c != Double.class) && (c != Boolean.class) && (c != String.class)) {
                throw new ColumnFormatException("Storable.constructor: Illegal type \"" + c.toString() + "\"");
            } else if (c != columnValues.get(i).getClass()) {
                throw new ColumnFormatException("Storable.constructor: "
                                              + "The value type does not match the column type at " + i + " position");
            }
            ++i;
        }
        types = columnTypes;
        values = columnValues;
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
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if ((columnIndex < 0) || (columnIndex >= values.size())) {
            throw new IndexOutOfBoundsException("Storable.setColumnAt: Incorrect index");
        }
        if ((value != null) && (value.getClass() != types.get(columnIndex))) {
            throw new ColumnFormatException("Storable.setColumnAt: The value type does not match the column type");
        }
        values.set(columnIndex, value);
    }

    /**
     * Возвращает значение из данной колонки, не приводя его к конкретному типу.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, без приведения типа. Может быть null.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if ((columnIndex < 0) || (columnIndex >= values.size())) {
            throw new IndexOutOfBoundsException("Storable.getColumnAt: Incorrect index");
        }
        return values.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Integer.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Integer. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return ((Integer) getAt(columnIndex, Integer.class));
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Long.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Long. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return ((Long) getAt(columnIndex, Long.class));
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Byte.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Byte. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return ((Byte) getAt(columnIndex, Byte.class));
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Float.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Float. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return ((Float) getAt(columnIndex, Float.class));
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Double.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Double. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return ((Double) getAt(columnIndex, Double.class));
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Boolean.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Boolean. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return ((Boolean) getAt(columnIndex, Boolean.class));
    }

    /**
     * Возвращает значение из данной колонки, приведя его к String.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к String. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return ((String) getAt(columnIndex, String.class));
    }

    public Object getAt(int columnIndex, Class<?> type) throws ColumnFormatException, IndexOutOfBoundsException {
        if ((columnIndex < 0) || (columnIndex >= values.size())) {
            throw new IndexOutOfBoundsException("Storable.get" + type.toString() + "At: Incorrect index");
        }
        if ((values.get(columnIndex) != null) && (types.get(columnIndex) != type)) {
            throw new ColumnFormatException("Storable.get" + type.toString()
                                            + "At: The value type does not match the column type");
        }
        return values.get(columnIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName() + "[");
        for (Object o : values) {
            if (o != null) {
                sb.append(o.toString());
            }
            sb.append(',');
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }
}

