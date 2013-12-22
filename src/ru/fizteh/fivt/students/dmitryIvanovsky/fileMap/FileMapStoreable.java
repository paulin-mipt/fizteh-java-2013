package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class FileMapStoreable implements Storeable {

    private List<Object> column = new ArrayList<>();
    private List<Class<?>> columnType;

    public FileMapStoreable(List<Class<?>> columnType) {
        this.columnType = columnType;
        for (Class<?> col : columnType) {
            column.add(null);
        }
    }

    private void checkColumnIndexBounds(int columnIndex) {
        if (columnIndex < 0 || columnIndex > column.size()) {
            throw new IndexOutOfBoundsException("index is out of bounds");
        }
    }

    private void checkColumnFormat(int columnIndex, Class<?> valueClass) {
        if (!valueClass.equals(columnType.get(columnIndex))) {
            String message = String.format("wrong column format index %d %s", columnIndex, valueClass.toString());
            throw new ColumnFormatException(message);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value == null) {
            column.set(columnIndex, null);
            return;
        }
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, value.getClass());
        column.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        return column.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Integer.class);
        return Integer.class.cast(column.get(columnIndex));
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Long.class);
        return Long.class.cast(column.get(columnIndex));
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Byte.class);
        return Byte.class.cast(column.get(columnIndex));
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Float.class);
        return Float.class.cast(column.get(columnIndex));
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Double.class);
        return Double.class.cast(column.get(columnIndex));
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Boolean.class);
        return Boolean.class.cast(column.get(columnIndex));
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, String.class);
        return String.class.cast(column.get(columnIndex));
    }

    public String messageEqualsType(List<Class<?>> list) {
        if (list.size() != columnType.size()) {
            return "wrong type count of arguments different";
        }
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getName().equals(columnType.get(i).getName())) {
                return String.format("wrong type %s not exist", list.get(i).getName());
            }
        }
        return "";
    }

    public int hashCode() {
        int hash = 0;
        for (Object col : column) {
            hash = hash * 17 + col.hashCode();
        }
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        FileMapStoreable line = (FileMapStoreable) obj;
        for (int i = 0; i < line.columnType.size(); i++) {
            if (!line.columnType.get(i).getName().equals(columnType.get(i).getName())) {
                return false;
            }
        }
        for (int i = 0; i < line.column.size(); i++) {
            if (line.getColumnAt(i) != null && this.getColumnAt(i) != null) {
                if (!line.getColumnAt(i).equals(this.getColumnAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder listValue = new StringBuilder();
        for (int i = 0; i < columnType.size(); ++i) {
            if (getColumnAt(i) != null) {
                listValue.append(getColumnAt(i).toString());
            }
            if (i != columnType.size() - 1) {
                listValue.append(",");
            }
        }
        return String.format("%s[%s]", getClass().getSimpleName(), listValue);
        //return this.getClass().getSimpleName() + "[" + listValue.toString() + "]";
    }
}
