package ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;
import java.util.List;

public class StoreableImplementation implements Storeable {
    private final Table table;
    private final List<Class<?>> columnTypes;
    private List<Object> values;

    StoreableImplementation(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }
        this.table = table;
        columnTypes = new ArrayList<>();
        values = new ArrayList<>();
        int columnNumber = table.getColumnsCount();
        for (int columnIndex = 0; columnIndex < columnNumber; ++columnIndex) {
            columnTypes.add(table.getColumnType(columnIndex));
            values.add(null);
        }
    }

    private Object getSpecifiedValueAt(int columnIndex, Class<?> specifiedClass) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object toReturn = getColumnAt(columnIndex);
        if (toReturn != null && !toReturn.getClass().equals(specifiedClass)) {
            throw new ColumnFormatException(String.format("Requested type and type of column %s not match",
                    columnIndex));
        }
        return toReturn;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= table.getColumnsCount()) {
            if (columnIndex < 0) {
                throw new IndexOutOfBoundsException("Negative index");
            } else {
                throw new IndexOutOfBoundsException("Index is bigger than the size");
            }
        }
        if (value != null && !value.getClass().equals(columnTypes.get(columnIndex))) {
            throw new ColumnFormatException(String.format("Requested type and type of column %s not match",
                    columnIndex));
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= table.getColumnsCount()) {
            if (columnIndex < 0) {
                throw new IndexOutOfBoundsException("Negative index");
            } else {
                throw new IndexOutOfBoundsException("Index is bigger than the size");
            }
        }
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Integer) getSpecifiedValueAt(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Long) getSpecifiedValueAt(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Byte) getSpecifiedValueAt(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Float) getSpecifiedValueAt(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Double) getSpecifiedValueAt(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Boolean) getSpecifiedValueAt(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (String) getSpecifiedValueAt(columnIndex, String.class);
    }
}
