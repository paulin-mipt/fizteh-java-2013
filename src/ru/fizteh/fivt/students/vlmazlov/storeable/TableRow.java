package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class TableRow implements Storeable {
    private List<Object> values;
    private final List<Class<?>> valueTypes;

    public TableRow(List<Class<?>> valueTypes) {
        this.valueTypes = new ArrayList(valueTypes);
        values = new ArrayList(valueTypes.size());

        for (int i = 0; i < valueTypes.size(); ++i) {
            values.add(i, null);
        }
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null) {
            typeCheck(valueTypes.get(columnIndex), value.getClass());
        }

        values.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        typeCheck(Integer.class, valueTypes.get(columnIndex));
        return (Integer) getColumnAt(columnIndex);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        typeCheck(Long.class, valueTypes.get(columnIndex));
        return (Long) getColumnAt(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        typeCheck(Byte.class, valueTypes.get(columnIndex));
        return (Byte) getColumnAt(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        typeCheck(Float.class, valueTypes.get(columnIndex));
        return (Float) getColumnAt(columnIndex);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        typeCheck(Double.class, valueTypes.get(columnIndex));
        return (Double) getColumnAt(columnIndex);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        typeCheck(Boolean.class, valueTypes.get(columnIndex));
        return (Boolean) getColumnAt(columnIndex);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        typeCheck(String.class, valueTypes.get(columnIndex));
        return (String) getColumnAt(columnIndex);
    }

    private void typeCheck(Class<?> type, Class<?> assignedValue) throws ColumnFormatException {
        if (!type.isAssignableFrom(assignedValue)) {
            throw new ColumnFormatException();
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("[");

        for (Object value : values) {
            if (value != null) {
                builder.append(value);
            }

            builder.append(",");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }
}

