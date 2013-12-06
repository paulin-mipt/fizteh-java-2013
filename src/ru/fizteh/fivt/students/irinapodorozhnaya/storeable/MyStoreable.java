package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.util.ArrayList;
import java.util.List;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class MyStoreable implements Storeable{

    private final List<Object> values;    
    private final Table table;

    public MyStoreable(Table table) {
        this.table = table;
        values = new ArrayList<>(table.getColumnsCount());
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values.add(null);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append('[');
        for (Object v: values) {
            if (v != null) {
                builder.append(v.toString());
            }
            builder.append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(']');

        return  new String(builder);
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        MyStoreable st = (MyStoreable) o;
        return values.equals(st.values);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {

        if (value != null && !value.getClass().equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (values.isEmpty()) {
            return null;
        }
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        
        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Integer.class)) {
            throw new ColumnFormatException();
        }
        return (Integer) value;
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Long.class)) {
            throw new ColumnFormatException();
        }
        return (Long) value;
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Byte.class)) {
            throw new ColumnFormatException();
        }
        return (Byte) value;
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Float.class)) {
            throw new ColumnFormatException();
        }
        return (Float) value;
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Double.class)) {
            throw new ColumnFormatException();
        }
        return (Double) value;
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Boolean.class)) {
            throw new ColumnFormatException();
        }
        return (Boolean) value;
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(String.class)) {
            throw new ColumnFormatException();
        }
        return (String) value;
    }
}
