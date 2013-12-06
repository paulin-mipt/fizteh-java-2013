package ru.fizteh.fivt.students.vyatkina.database.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class StorableRow implements Storeable {

    private StorableRowShape shape;
    private List<Object> values = new ArrayList<>();

    public StorableRow(StorableRowShape shape) {
        this.shape = shape;
        for (int i = 0; i < shape.getColumnsCount(); i++) {
            values.add(null);
        }
    }

    public StorableRow(StorableRowShape shape, List<Object> values) {
        this(shape);
        fillWith(values);
    }

    public void fillWith(List<?> values) {
        if (values.size() < shape.getColumnsCount()) {
            throw new IndexOutOfBoundsException("Too small values");
        }
        for (int i = 0; i < shape.getColumnsCount(); i++) {
            setColumnAt(i, values.get(i));
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null) {
            shape.columnFormatCheck(columnIndex, value.getClass());
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        shape.indexInBoundsCheck(columnIndex);
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        shape.columnFormatCheck(columnIndex, Integer.class);
        return Integer.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        shape.columnFormatCheck(columnIndex, Long.class);
        return Long.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        shape.columnFormatCheck(columnIndex, Byte.class);
        return Byte.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        shape.columnFormatCheck(columnIndex, Float.class);
        return Float.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        shape.columnFormatCheck(columnIndex, Double.class);
        return Double.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        shape.columnFormatCheck(columnIndex, Boolean.class);
        return Boolean.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        shape.columnFormatCheck(columnIndex, String.class);
        return String.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StorableRow that = (StorableRow) o;

        if (shape != null ? !shape.equals(that.shape) : that.shape != null) {
            return false;
        }
        if (values != null ? !values.equals(that.values) : that.values != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = shape != null ? shape.hashCode() : 0;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("[");
        boolean isFirst = true;
        for (Object value : values) {
            if (!isFirst) {
                sb.append(",");
            } else {
                isFirst = false;
            }
            if (value != null) {
                sb.append(value);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
