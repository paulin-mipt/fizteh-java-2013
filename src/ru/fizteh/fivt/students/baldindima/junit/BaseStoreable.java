package ru.fizteh.fivt.students.baldindima.junit;

import org.json.JSONObject;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;
import java.util.List;

public class BaseStoreable implements Storeable {
    private List<Class<?>> types = new ArrayList<>();
    private List<Object> values = new ArrayList<>();

    public BaseStoreable(Table table) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
            values.add(null);
            
        }
    }

    public void setValues(List<?> nValues) {
        if (nValues == null) {
            throw new IndexOutOfBoundsException("list of values is null");
        }
        if (nValues.size() != types.size()) {
            throw new IndexOutOfBoundsException("wrong number of values");
        }

        for (int i = 0; i < nValues.size(); ++i) {
            Object value = cast(types.get(i), nValues.get(i));
            if (value != null && value.getClass() != (types.get(i))) {
                throw new ColumnFormatException(nValues.get(i).toString() + " must be " + types.get(i)
                        + " but it is " + nValues.get(i).getClass());
            }

            values.set(i,value);
        }

    }

    Object cast(Class<?> type, Object value) {
        if (value == null) {
            return null;
        }

        Class<?> valueType = value.getClass();
        if (valueType == JSONObject.NULL.getClass() || JSONObject.NULL == value) {
            return null;
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
            if (value == Float.class) {
                return value;
            }
            throw new ColumnFormatException("Wrong type");
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
            throw new ColumnFormatException("Wrong type");
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
                    throw new ColumnFormatException("Too big number for integer type");
                }
            }
            throw new ColumnFormatException("Wrong type");
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
            throw new ColumnFormatException("Wrong type");
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
            throw new ColumnFormatException("Wrong type");
        }


        if (type == String.class) {
            return value.toString();
        }

        return value;
    }

    public static boolean isCorrectStoreable(Storeable storeable, Table table) {
        if (storeable == null) {
            throw new IllegalArgumentException("Storeable must be not null");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }

        if (!isCorrectSize(storeable, table)) {
            return false;
        }

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (storeable.getColumnAt(i) != null
                    && storeable.getColumnAt(i).getClass() != table.getColumnType(i)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCorrectSize(Storeable storeable, Table table) {
        try {
            storeable.getColumnAt(table.getColumnsCount() - 1);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        try {
            storeable.getColumnAt(table.getColumnsCount());
        } catch (IndexOutOfBoundsException e) {
            return true;
        }

        return false;
    }

    void checkIndex(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= types.size()) {
            throw new IndexOutOfBoundsException("wrong index");
        }
    }

    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (value == null || value == JSONObject.NULL) {
            value = null;
        }
        value = cast(types.get(columnIndex), value);
        if (value != null && (value.getClass() != (types.get(columnIndex)))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }
        values.set(columnIndex, value);
    }


    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);

        return values.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (!Integer.class.equals(types.get(columnIndex))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }
        return (Integer) values.get(columnIndex);
    }


    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (!Long.class.equals(types.get(columnIndex))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }
        return (long) values.get(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (!Byte.class.equals(types.get(columnIndex))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }
        return (byte) values.get(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (!Float.class.equals(types.get(columnIndex))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }
        return (float) values.get(columnIndex);
    }


    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (!Double.class.equals(types.get(columnIndex))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }
        return (double) values.get(columnIndex);
    }


    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (!Boolean.class.equals(types.get(columnIndex))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }

        return (boolean) values.get(columnIndex);
    }


    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (!String.class.equals(types.get(columnIndex))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }

        return (String) values.get(columnIndex);
    }
}