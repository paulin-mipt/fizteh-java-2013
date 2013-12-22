package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseStoreable implements Storeable {
    List<Class<?>> classes = new ArrayList<>();
    List<Object> columns = new ArrayList<>();

    public DatabaseStoreable(List<Class<?>> classList) {
        this.classes = classList;
        for (int i = 0; i < classes.size(); i++) {
            columns.add(null);
        }
    }

    @Override
    public int hashCode() {
        return this.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Storeable st = (Storeable) obj;
        for (int i = 0; i < columns.size(); i++) {
            Object value1 = this.getColumnAt(i);
            Object value2 = st.getColumnAt(i);
            if (value1 == null && value2 == null) {
                continue;
            }
            if (value1 == null || value2 == null) {
                return false;
            }
            if (!value1.equals(value2)) {
                return false;
            }
        }
        return true;
    }

    public void indexOfBounds(int columnNum) {
        if (columnNum < 0 || columnNum >= classes.size()) {
            throw new IndexOutOfBoundsException(String.format("Index %d is out of bounds", columnNum));
        }
    }

    public void setColumnAt(int columnNum, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        indexOfBounds(columnNum);
        if (value != null) {
            isColumnTypeValid(columnNum, value.getClass());
            if ((value.getClass().getName().equals("java.lang.String"))
                    && ((String) value).trim().isEmpty()) {
                columns.set(columnNum, value);
                return;
            }
            isColumnTypeValid(columnNum, value.getClass());
        }
        columns.set(columnNum, value);
    }

    public Object getColumnAt(int columnNum) throws IndexOutOfBoundsException {
        indexOfBounds(columnNum);
        return columns.get(columnNum);
    }

    public Integer getIntAt(int columnNum) throws ColumnFormatException, IndexOutOfBoundsException {
        indexOfBounds(columnNum);
        isColumnTypeValid(columnNum, Integer.class);
        return (Integer) columns.get(columnNum);
    }

    public Long getLongAt(int columnNum) throws ColumnFormatException, IndexOutOfBoundsException {
        indexOfBounds(columnNum);
        isColumnTypeValid(columnNum, Long.class);
        return (Long) columns.get(columnNum);
    }

    public Byte getByteAt(int columnNum) throws ColumnFormatException, IndexOutOfBoundsException {
        indexOfBounds(columnNum);
        isColumnTypeValid(columnNum, Byte.class);
        return (Byte) columns.get(columnNum);
    }

    public Float getFloatAt(int columnNum) throws ColumnFormatException, IndexOutOfBoundsException {
        indexOfBounds(columnNum);
        isColumnTypeValid(columnNum, Float.class);
        return (Float) columns.get(columnNum);
    }

    public Double getDoubleAt(int columnNum) throws ColumnFormatException, IndexOutOfBoundsException {
        indexOfBounds(columnNum);
        isColumnTypeValid(columnNum, Double.class);
        return (Double) columns.get(columnNum);
    }

    public Boolean getBooleanAt(int columnNum) throws ColumnFormatException, IndexOutOfBoundsException {
        indexOfBounds(columnNum);
        isColumnTypeValid(columnNum, Boolean.class);
        return (Boolean) columns.get(columnNum);
    }

    public String getStringAt(int columnNum) throws ColumnFormatException, IndexOutOfBoundsException {
        indexOfBounds(columnNum);
        isColumnTypeValid(columnNum, String.class);
        return (String) columns.get(columnNum);
    }

    public void addColumn(Class<?> columnType) {
        classes.add(columnType);
        columns.add(null);
    }

    public void setColumns(List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values.size() != classes.size()) {
            throw new IndexOutOfBoundsException();
        }
        columns.clear();

        for (int i = 0; i < values.size(); i++) {
            columns.add(values.get(i));
        }
    }

    private void isColumnTypeValid(int columnIndex, Class<?> value) throws ColumnFormatException {
        if (!value.isAssignableFrom(classes.get(columnIndex))) {
            throw new ColumnFormatException(String.format("Incorrect type: expected %s, but is %s",
                    classes.get(columnIndex).getName(), value.getName()));
        }
    }

    public static String join(List<?> list, boolean nameNulls, String delimiter) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final Object listEntry : list) {
            if (!first) {
                sb.append(delimiter);
            }
            first = false;
            if (listEntry == null) {
                if (nameNulls) {
                    sb.append("null");
                }
            } else {
                sb.append(listEntry.toString());
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), join(columns, false, ","));
    }
}
