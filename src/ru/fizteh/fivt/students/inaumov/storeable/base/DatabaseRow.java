package ru.fizteh.fivt.students.inaumov.storeable.base;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.inaumov.storeable.StoreableUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRow implements Storeable {
    final List<Class<?>> classes = new ArrayList<Class<?>>();
    List<Object> columns = new ArrayList<Object>();

    public DatabaseRow(List<Class<?>> classes) {
        this.classes.addAll(classes);

        for (int i = 0; i < classes.size(); ++i) {
            columns.add(null);
        }
    }

    public DatabaseRow() {

    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        if (value != null) {
            checkColumnType(columnIndex, value.getClass());
            try {
                StoreableUtils.isValueCorrect(value, value.getClass());
            } catch (ParseException e) {
                throw new IllegalArgumentException("error: incorrect value: " + e.getMessage());
            }
        }

        columns.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) {
        checkBounds(columnIndex);

        return columns.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Integer.class);

        return (Integer) columns.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Long.class);

        return (Long) columns.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Byte.class);

        return (Byte) columns.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Float.class);

        return (Float) columns.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Double.class);

        return (Double) columns.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Boolean.class);

        return (Boolean) columns.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, String.class);

        return (String) columns.get(columnIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        DatabaseRow otherStoreable = (DatabaseRow) obj;

        return otherStoreable.columns.equals(columns) && otherStoreable.classes.equals(classes);
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

        for (int index = 0; index < values.size(); ++index) {
            columns.add(values.get(index));
        }
    }

    private void checkBounds(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= classes.size()) {
            throw new IndexOutOfBoundsException(String.format("index out of bound: %d", index));
        }
    }

    private void checkColumnType(int columnIndex, Class value) throws ColumnFormatException {
        if (!value.isAssignableFrom(classes.get(columnIndex))) {
            throw new ColumnFormatException(String.format("incorrect type: expected type: %s actual type: %s",
                    classes.get(columnIndex).getName(), value.getName()));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + StoreableUtils.valuesTypeNamesToString(columns, false, ",") + "]";
    }
}
