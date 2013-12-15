package ru.fizteh.fivt.students.elenav.storeable;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class MyStoreable implements Storeable {

    private List<Object> listValues = new ArrayList<>();
    public List<Class<?>> listTypes = new ArrayList<>();
    
    public MyStoreable(Table t) {
        int n = t.getColumnsCount();
        for (int i = 0; i < t.getColumnsCount(); ++i) {
            listTypes.add(t.getColumnType(i));
        }
        listValues = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            listValues.add(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        MyStoreable st = (MyStoreable) o;
        return st.listTypes.equals(listTypes) && st.listValues.equals(listValues);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public MyStoreable(List<Class<?>> classes) {
        listTypes = classes;
        for (int i = 0; i < listTypes.size(); ++i) {
            listValues.add(null);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object o : listValues) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            if (o != null) {
                sb.append(o.toString());
            }
        }
        return getClass().getSimpleName() + "[" + sb.toString() + "]";
    }
    
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null) {
            if (!value.getClass().isAssignableFrom((listTypes.get(columnIndex)))) {
                throw new ColumnFormatException(columnIndex + " column: invalid format");
            }
        }
        listValues.set(columnIndex, value);
        
    }
    
    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return listValues.isEmpty() ? null : listValues.get(columnIndex);
    }
    
    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object type = listValues.get(columnIndex);
        if (type.getClass().equals(Integer.class)) {
            return Integer.class.cast(type);
        } else {
            throw new ColumnFormatException();
        }
    }
    
    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object type = listValues.get(columnIndex);
        if (type.getClass().equals(Long.class)) {
            return Long.class.cast(type);
        } else {
            throw new ColumnFormatException();
        }
    }
    
    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object type = listValues.get(columnIndex);
        if (type.getClass().equals(Byte.class)) {
            return Byte.class.cast(type);
        } else {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object type = listValues.get(columnIndex);
        if (type.getClass().equals(Float.class)) {
            return Float.class.cast(type);
        } else {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object type = listValues.get(columnIndex);
        if (type.getClass().equals(Double.class)) {
            return Double.class.cast(type);
        } else {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Class<?> type = listTypes.get(columnIndex);
        if (type.equals(Boolean.class)) {
            return Boolean.class.cast(listValues.get(columnIndex));
        } else {
            throw new ColumnFormatException();
        }
    }
    
    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        Object type = listValues.get(columnIndex);
        if (type.getClass().equals(String.class)) {
            return String.class.cast(type);
        } else {
            throw new ColumnFormatException();
        }
    }
    
}
