package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class MyStoreable implements Storeable {

    private List<Object> columns = new ArrayList<Object>();
    private List<Class<?>> columnTypes = new ArrayList<Class<?>>();

    public MyStoreable(List<Class<?>> inColumnTypes) {

        columnTypes = inColumnTypes;
        for (Class<?> columnType : columnTypes) {
            columns.add(null);
        }
    }

    public void checkingBorderToIndex(int index) {

        if (index < 0 || index > columns.size()) {
            throw new IndexOutOfBoundsException("incorrect border to index");
        }
    }

    public void checkingFormatOfColumn(int index, Class<?> inObject) {

        if (!inObject.equals(columnTypes.get(index))) {
            throw new ColumnFormatException("incorrect type of column");
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {

        checkingBorderToIndex(columnIndex);
        if (value != null) {
            checkingFormatOfColumn(columnIndex, value.getClass());
        }
        columns.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {

        checkingBorderToIndex(columnIndex);
        return columns.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {


        checkingBorderToIndex(columnIndex);
        checkingFormatOfColumn(columnIndex, Integer.class);
        return (Integer) columns.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        checkingBorderToIndex(columnIndex);
        checkingFormatOfColumn(columnIndex, Long.class);
        return (Long) columns.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        checkingBorderToIndex(columnIndex);
        checkingFormatOfColumn(columnIndex, Byte.class);
        return (Byte) columns.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        checkingBorderToIndex(columnIndex);
        checkingFormatOfColumn(columnIndex, Float.class);
        return (Float) columns.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        checkingBorderToIndex(columnIndex);
        checkingFormatOfColumn(columnIndex, Double.class);
        return (Double) columns.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        checkingBorderToIndex(columnIndex);
        checkingFormatOfColumn(columnIndex, Boolean.class);
        return (Boolean) columns.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        checkingBorderToIndex(columnIndex);
        checkingFormatOfColumn(columnIndex, String.class);
        return (String) columns.get(columnIndex);
    }
}
