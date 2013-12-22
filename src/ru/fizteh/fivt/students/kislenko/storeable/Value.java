package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;

public class Value implements Storeable {
    private ArrayList<Class<?>> types;
    private ArrayList<Object> columns = new ArrayList<Object>();

    public Value(ArrayList<Class<?>> columnTypes) {
        types = columnTypes;
        for (int i = 0; i < types.size(); ++i) {
            columns.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= types.size()) {
            throw new IndexOutOfBoundsException("Incorrect number of column to set.");
        }
        if (value != null && value.getClass() != types.get(columnIndex) && !(types.get(columnIndex) == Long.class
                && value.getClass() == Integer.class)) {
            throw new ColumnFormatException("Incorrect type of setting value.");
        }
        columns.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Incorrect number of column to get.");
        }
        return columns.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Incorrect number of column to get.");
        }
        if (!types.get(columnIndex).equals(Integer.class)) {
            throw new ColumnFormatException("Can't get int value, when it's not int.");
        }
        return (Integer) columns.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Incorrect number of column to get.");
        }
        if (!types.get(columnIndex).equals(Long.class)) {
            throw new ColumnFormatException("Can't get long value, when it's not long.");
        }
        return (Long) columns.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Incorrect number of column to get.");
        }
        if (!types.get(columnIndex).equals(Byte.class)) {
            throw new ColumnFormatException("Can't get byte value, when it's not byte.");
        }
        return (Byte) columns.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Incorrect number of column to get.");
        }
        if (!types.get(columnIndex).equals(Float.class)) {
            throw new ColumnFormatException("Can't get float value, when it's not float.");
        }
        return (Float) columns.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Incorrect number of column to get.");
        }
        if (!types.get(columnIndex).equals(Double.class)) {
            throw new ColumnFormatException("Can't get double value, when it's not double.");
        }
        return (Double) columns.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Incorrect number of column to get.");
        }
        if (!types.get(columnIndex).equals(Boolean.class)) {
            throw new ColumnFormatException("Can't get boolean value, when it's not boolean.");
        }
        return (Boolean) columns.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Incorrect number of column to get.");
        }
        if (!types.get(columnIndex).equals(String.class)) {
            throw new ColumnFormatException("Can't get string value, when it's not string.");
        }
        return (String) columns.get(columnIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName() + "[");
        for (int i = 0; i < types.size(); ++i) {
            if (getColumnAt(i) != null) {
                sb.append(getColumnAt(i).toString());
            }
            if (i < types.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
