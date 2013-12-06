package ru.fizteh.fivt.students.msandrikova.storeable;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class TableRow implements Storeable {
    private List<Class<?>> columnTypes = new ArrayList<Class<?>>();
    private List<Object> row = new ArrayList<Object>();
    
    public TableRow(List<Class<?>> columnTypes) {
        this.columnTypes = columnTypes;
        for (int i = 0; i < columnTypes.size(); ++i) {
            row.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException("Column index can not be less then "
                    + "0 and more then types amount.");
        }
        if (value != null && !value.getClass().equals(this.columnTypes.get(columnIndex))) {
            throw new ColumnFormatException("Value's class must be equal to column type");
        }
        row.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException("Column index can not be less then "
                    + "0 and more then types amount.");
        }
        return row.get(columnIndex);
    }
    
    private Object getClassAt(int columnIndex, Class<?> classType) 
            throws ColumnFormatException, IndexOutOfBoundsException  {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException("Column index can not "
                    + "be less then 0 and more then types amount.");
        }
        if (!classType.equals(this.columnTypes.get(columnIndex))) {
            throw new ColumnFormatException("Requested class must be equal to column type");
        }
        return row.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object o = this.getClassAt(columnIndex, Integer.class);
        if (o == null) {
            return null;
        }
        return Integer.parseInt(o.toString());
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object o = this.getClassAt(columnIndex, Long.class);
        if (o == null) {
            return null;
        }
        return Long.parseLong(o.toString());
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object o = this.getClassAt(columnIndex, Byte.class);
        if (o == null) {
            return null;
        }
        return Byte.parseByte(o.toString());
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object o = this.getClassAt(columnIndex, Float.class);
        if (o == null) {
            return null;
        }
        return Float.parseFloat(o.toString());
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object o = this.getClassAt(columnIndex, Double.class);
        if (o == null) {
            return null;
        }
        return Double.parseDouble(o.toString());
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object o = this.getClassAt(columnIndex, Boolean.class);
        if (o == null) {
            return null;
        }
        return Boolean.parseBoolean(o.toString());
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object o = this.getClassAt(columnIndex, String.class);
        if (o == null) {
            return null;
        }
        return o.toString();
    }
    
    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();
        String answer = className + "[";
        boolean first = true;
        for (Object o : this.row) {
            if (!first) {
                answer += ",";
            }
            first = false;
            if (o != null) {
                answer += o.toString();
            }
        }
        answer += "]";
        return answer;
    }

}
