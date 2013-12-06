package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import ru.fizteh.fivt.storage.structured.*;

public class MyStoreable implements Storeable {
    private ArrayList<Class<?>> columnTypes;
    private Object[] objectList;
    
    public MyStoreable(List<Class<?>> types) {
        columnTypes = new ArrayList<Class<?>>(types);
        objectList = new Object[types.size()];
    }
    
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objectList.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException("requested index is out of bounds");
        }
        
        Object newValue = castType(value, columnTypes.get(columnIndex));
        objectList[columnIndex] = newValue;
    }

    public ArrayList<Class<?>> getColumnTypes() {
        return new ArrayList<>(columnTypes);
    }

    public int size() {
        return objectList.length;
    }
    
    private void checkIndex(int index) {
        if (index >= objectList.length || index < 0) {
            throw new IndexOutOfBoundsException("requested index is out of bounds");
        }
    }
    
    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return objectList[columnIndex];
    }
    
    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (Integer.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("expected " 
                    + columnTypes.get(columnIndex).toString() + ", int requested");
        }
        return (Integer) objectList[columnIndex];
    }
    
    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (Long.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("expected " 
                    + columnTypes.get(columnIndex).toString() + ", long requested");
        }
        return (Long) objectList[columnIndex];
    }
    
    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (Byte.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("expected " 
                    + columnTypes.get(columnIndex).toString() + ", byte requested");
        }
        return (Byte) objectList[columnIndex];
    }
    
    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (Float.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("expected " 
                    + columnTypes.get(columnIndex).toString() + ", float requested");
        }
        return (Float) objectList[columnIndex];
    }
    
    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (Double.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("expected " 
                    + columnTypes.get(columnIndex).toString() + ", double requested");
        }
        return (Double) objectList[columnIndex];
    }
    
    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (Boolean.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("expected " 
                    + columnTypes.get(columnIndex).toString() + ", boolean requested");
        }
        return (Boolean) objectList[columnIndex];
    }
    
    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (String.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("expected " 
                    + columnTypes.get(columnIndex).toString() + ", string requested");
        }
        return (String) objectList[columnIndex];
    }
    
    private static Object castType(Object object, Class<?> expectedClass) throws ColumnFormatException {
        if (object == null || object == JSONObject.NULL) {
            return null;
        }
        if (expectedClass == Integer.class) {
            if (object.getClass() == Integer.class) {
                return object;
             }
        } else if (expectedClass == Long.class) {
            if (object.getClass() == Long.class) {
               return object;
            } 
            if (object.getClass() == Integer.class) {
                return Long.valueOf(((Integer) object).longValue());
            }
        } else if (expectedClass == Byte.class) {
            if (object.getClass() == Byte.class) {
                return object;
            }
            if (object.getClass() == Integer.class) {
                Integer number = (Integer) object;
                if (number <= Byte.MAX_VALUE && number >= Byte.MIN_VALUE) {
                    return Byte.valueOf(number.byteValue());
                }
            }
        } else if (expectedClass == Double.class) {
            if (object.getClass() == Double.class) {
                return object;
            }
        } else if (expectedClass == Float.class) {
            if (object.getClass() == Float.class) {
                return object;
            }
            if (object.getClass() == Double.class) {
                return ((Double) object).floatValue();
            }
        } else if (expectedClass == String.class) {
            if (object.getClass() == String.class) {
                return object;
            }
        } else if (expectedClass == Boolean.class) {
            if (object.getClass() == Boolean.class) {
                return object;
            }
        }
        throw new ColumnFormatException("expected " + expectedClass.toString()
                + ", " + object.getClass().toString() + " found");
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getClass().getSimpleName() + "[");
        for (int i = 0; i < size(); i++) {
            if (objectList[i] != null) {
                result.append(objectList[i].toString());
            }
            if (i != size() - 1) {
                result.append(",");
            }
        }
        result.append("]");
        return result.toString();
    }
    
    @Override 
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override 
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return toString().equals(((Storeable) obj).toString());
    }
}
