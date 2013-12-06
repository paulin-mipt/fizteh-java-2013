package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FixedList implements Storeable {
    public static final Class<?>[] CLASSES = new Class<?>[] {
            Integer.class,
            Long.class,
            Byte.class,
            Float.class,
            Double.class,
            Boolean.class,
            String.class
    };

    final ArrayList<Class<?>> columnTypes;
    Object[] objects;

    public FixedList(List<Class<?>> columnTypes) {
        this.columnTypes = new ArrayList<Class<?>>(columnTypes);
        objects = new Object[columnTypes.size()];
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    objects.length, columnIndex));
        }
        if (value != null && value.getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found",
                    columnTypes.get(columnIndex).toString(), value.getClass().toString()));
        }
        objects[columnIndex] = value;
    }

    public ArrayList<Class<?>> getColumnTypes() {
        return new ArrayList<>(columnTypes);
    }

    public int size() {
        return objects.length;
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    objects.length, columnIndex));
        }
        return objects[columnIndex];
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    objects.length, columnIndex));
        }
        if (Integer.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found",
                    columnTypes.get(columnIndex).toString(), Integer.class.toString()));
        }
        return (Integer) objects[columnIndex];
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    objects.length, columnIndex));
        }
        if (Long.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found",
                    columnTypes.get(columnIndex).toString(), Long.class.toString()));
        }
        return (Long) objects[columnIndex];
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    objects.length, columnIndex));
        }
        if (Byte.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found",
                    columnTypes.get(columnIndex).toString(), Byte.class.toString()));
        }
        return (Byte) objects[columnIndex];
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    objects.length, columnIndex));
        }
        if (Float.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found",
                    columnTypes.get(columnIndex).toString(), Float.class.toString()));
        }
        return (Float) objects[columnIndex];
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    objects.length, columnIndex));
        }
        if (Double.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found",
                    columnTypes.get(columnIndex).toString(), Double.class.toString()));
        }
        return (Double) objects[columnIndex];
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    objects.length, columnIndex));
        }
        if (Boolean.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found",
                    columnTypes.get(columnIndex).toString(), Boolean.class.toString()));
        }
        return (Boolean) objects[columnIndex];
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    objects.length, columnIndex));
        }
        if (String.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found",
                    columnTypes.get(columnIndex).toString(), String.class.toString()));
        }
        return (String) objects[columnIndex];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                sb.append(objects[i].toString());
            }
            if (i != objects.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return String.format("%s%s", this.getClass().getSimpleName(), sb.toString());
    }

    public void fromString(String serialized) {
        if (serialized == null) {
            throw new IllegalArgumentException("Null string as argument");
        }
        try {
            JSONArray array = new JSONArray(serialized);
            if (array.length() != objects.length) {
                throw new JSONException("array size mismatch");
            }
            for (int i = 0; i < objects.length; i++) {
                Object object = array.get(i);
                if (array.get(i).equals(null)) {
                    objects[i] = null;
                } else if (columnTypes.get(i) == Integer.class) {
                    if (object.getClass() == Integer.class) {
                        objects[i] = object;
                    } else {
                        throw new ColumnFormatException("Type mismatch");
                    }
                } else if (columnTypes.get(i) == Long.class) {
                    if (object.getClass() == Long.class) {
                        objects[i] = object;
                    } else if (object.getClass() == Integer.class) {
                        objects[i] = Long.valueOf(((Integer) object).longValue());
                    } else {
                        throw new ColumnFormatException("Type mismatch");
                    }
                } else if (columnTypes.get(i) == Byte.class) {
                    if (object.getClass() == Integer.class) {
                        Integer number = (Integer) object;
                        if (number > Byte.MAX_VALUE || number < Byte.MIN_VALUE) {
                            throw new ColumnFormatException("Type mismatch");
                        }
                        objects[i] = number;
                    } else {
                        throw new ColumnFormatException("Type mismatch");
                    }
                } else if (columnTypes.get(i) == Float.class) {
                    if (object.getClass() == Double.class) {
                        objects[i] = Float.valueOf(((Double) object).floatValue());
                    } else {
                        throw new ColumnFormatException("Type mismatch");
                    }
                } else if (columnTypes.get(i) == Double.class) {
                    if (object.getClass() == Double.class) {
                        objects[i] = object;
                    } else {
                        throw new ColumnFormatException("Type mismatch");
                    }
                } else if (columnTypes.get(i) == String.class) {
                    if (object.getClass() != String.class) {
                        throw new ColumnFormatException("Type mismatch");
                    } else {
                        objects[i] = object;
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(String.format("Error parsing string %s", serialized), e);
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("Error parsing string %s", serialized), e);
        }
    }

    public boolean equals(Object other) {
        if (other.getClass() == FixedList.class) {
            return equals((FixedList) other);
        }
        return super.equals(other);
    }

    public boolean equals(FixedList other) {
        if (other.size() != size()) {
            return false;
        }
        for (int i = 0; i < objects.length; i++) {
            if (other.getColumnAt(i) == null) {
                if (objects[i] != null) {
                    return false;
                }
            } else if (!other.getColumnAt(i).equals(objects[i])) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return toString().hashCode();
    }
}
