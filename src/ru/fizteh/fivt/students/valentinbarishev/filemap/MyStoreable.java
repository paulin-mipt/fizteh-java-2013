package ru.fizteh.fivt.students.valentinbarishev.filemap;

import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;
import java.util.List;

public class MyStoreable implements Storeable, AutoCloseable {
    private List<Class<?>> types = new ArrayList<>();
    private List<Object> values = new ArrayList<>();

    ClassState state = new ClassState(this);

    public MyStoreable(Table table, List<?> newValues) {
        if (newValues == null) {
            throw new IndexOutOfBoundsException("list of values cannot be null");
        }

        if (newValues.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("invalid number of values");
        }

        for (int i = 0; i < newValues.size(); ++i) {
            Object value = castTypes(table.getColumnType(i), newValues.get(i));
            if (value != null && value.getClass() != table.getColumnType(i)) {
                throw new ColumnFormatException(newValues.get(i).toString() + " must be " + table.getColumnType(i)
                        + " but it is " + newValues.get(i).getClass());
            }
            types.add(table.getColumnType(i));
            values.add(value);
        }
    }

    public MyStoreable(Table table) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
            values.add(null);
        }
    }


    void checkBounds(int index) {
        if (index < 0 || index >= types.size()) {
            throw new IndexOutOfBoundsException("index out of bounds!");
        }
    }

    void checkType(int index, Class<?> value) {
        if ((value != null) && (value != types.get(index))) {
            throw new ColumnFormatException();
        }
    }

    public int getSize() {
        return types.size();
    }

    Object castTypes(Class<?> type, Object value) {
        if (value == null) {
            return null;
        }

        Class<?> valueType = value.getClass();
        if (valueType == JSONObject.NULL.getClass() || JSONObject.NULL == value) {
            return null;
        }

        if (type == Integer.class) {
            if (valueType == Integer.class) {
                return value;
            }
            if (valueType == Byte.class) {
                return new Integer((byte) value);
            }
            if (valueType == Long.class) {
                long tmp = (long) value;
                if (tmp <= Integer.MAX_VALUE && tmp >= Integer.MIN_VALUE) {
                    return (int) tmp;
                } else {
                    throw new ColumnFormatException("Too big number for integer type: " + value.toString());
                }
            }
            throw new ColumnFormatException("Wrong type: " + valueType + " insted of Integer!");
        }

        if (type == Byte.class) {
            if (valueType == Byte.class) {
                return value;
            }
            if (valueType == Long.class || valueType == Integer.class) {
                long tmp;
                if (valueType == Long.class) {
                    tmp = (long) value;
                } else {
                    tmp = (int) value;
                }
                if (tmp <= Byte.MAX_VALUE && tmp >= Byte.MIN_VALUE) {
                    return (byte) tmp;
                } else {
                    throw new ColumnFormatException("Too big number for byte type: " + value.toString());
                }
            }
            throw new ColumnFormatException("Wrong type: " + valueType + " instead of Byte!");
        }

        if (type == Long.class) {
            if (valueType == Long.class) {
                return value;
            }
            if (valueType == Byte.class) {
                return new Long((byte) value);
            }
            if (valueType == Integer.class) {
                return new Long((int) value);
            }
            throw new ColumnFormatException("Wrong type: " + valueType + " instead of Long!");
        }

        if (type == Double.class) {
            if (valueType == Integer.class) {
                return new Double((int) value);
            }
            if (valueType == Byte.class) {
                return new Double((byte) value);
            }
            if (valueType == Long.class) {
                return new Double((long) value);
            }
            if (valueType == Float.class) {
                return new Double((float) value);
            }
            if (valueType == Double.class) {
                return value;
            }
            throw new ColumnFormatException("Wrong type: " + valueType + " instead of Double");
        }

        if (type == Float.class) {
            if (valueType == Integer.class) {
                return new Float((int) value);
            }
            if (valueType == Byte.class) {
                return new Float((byte) value);
            }
            if (valueType == Long.class) {
                return new Float((long) value);
            }
            if (valueType == Double.class) {
                return new Float((double) value);
            }
            if (valueType == Float.class) {
                return value;
            }
            throw new ColumnFormatException("Wrong type: " + valueType + " instead of Float");
        }

        if (type == String.class) {
            return value.toString();
        }

        return value;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        state.check();
        if (value == null || value == JSONObject.NULL) {
            value = null;
        }
        checkBounds(columnIndex);
        value = castTypes(types.get(columnIndex), value);
        if (value != null) {
            checkType(columnIndex, value.getClass());
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        state.check();
        checkBounds(columnIndex);
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        state.check();
        checkBounds(columnIndex);
        checkType(columnIndex, Integer.class);
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        state.check();
        checkBounds(columnIndex);
        checkType(columnIndex, Long.class);
        return (long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        state.check();
        checkBounds(columnIndex);
        checkType(columnIndex, Byte.class);
        return (byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        state.check();
        checkBounds(columnIndex);
        checkType(columnIndex, Float.class);
        return (float) values.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        state.check();
        checkBounds(columnIndex);
        checkType(columnIndex, Double.class);
        return (double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        state.check();
        checkBounds(columnIndex);
        checkType(columnIndex, Boolean.class);
        return (boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        state.check();
        checkBounds(columnIndex);
        checkType(columnIndex, String.class);
        return (String) values.get(columnIndex);
    }

    @Override
    public void close() {
        if (state.isClosed()) {
            return;
        }

        values.clear();
        types.clear();

        state.close();
    }

    @Override
    public String toString() {
        state.check();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); ++i) {
            if (values.get(i) != null) {
                builder.append(values.get(i).toString());
            }
            builder.append(",");
        }

        builder.deleteCharAt(builder.length() - 1);

        return String.format("%s[%s]", getClass().getSimpleName(), builder.toString());
    }
}
