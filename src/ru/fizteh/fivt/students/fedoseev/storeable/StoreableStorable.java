package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;
import java.util.List;

public class StoreableStorable implements Storeable {
    private final StoreableTable table;
    private final ArrayList<Object> columns;

    public StoreableStorable(Table table) {
        this.table = (StoreableTable) table;

        columns = new ArrayList<>();

        for (Class<?> ignored : this.table.getColumnTypes()) {
            columns.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        if (value != null && !value.getClass().equals(table.getColumnTypes().get(columnIndex))) {
            throw new ColumnFormatException("wrong type (SET ERROR: invalid type)");
        }

        columns.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);

        return columns.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnType(columnIndex, Integer.class);

        return (Integer) columns.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnType(columnIndex, Long.class);

        return (Long) columns.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnType(columnIndex, Byte.class);

        return (Byte) columns.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnType(columnIndex, Float.class);

        return (Float) columns.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnType(columnIndex, Double.class);

        return (Double) columns.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnType(columnIndex, Boolean.class);

        return (Boolean) columns.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnType(columnIndex, String.class);

        return (String) columns.get(columnIndex);
    }

    private void checkColumnIndex(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= table.getColumnTypes().size()) {
            throw new IndexOutOfBoundsException("SET | GET ERROR: invalid column index");
        }
    }

    private void checkColumnType(int columnIndex, Object value) throws ColumnFormatException {
        checkColumnIndex(columnIndex);

        if (!value.equals(table.getColumnTypes().get(columnIndex))) {
            throw new ColumnFormatException(String.format(
                    "wrong type (GET ERROR: incorrect type: expected %s instead of %s)",
                    table.getColumnTypes().get(columnIndex).getName(), value.getClass().getName())
            );
        }
    }

    public void setColumns(List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values.size() != table.getColumnTypes().size()) {
            throw new IndexOutOfBoundsException();
        }

        columns.clear();

        for (Object value : values) {
            columns.add(value);
        }
    }
}
