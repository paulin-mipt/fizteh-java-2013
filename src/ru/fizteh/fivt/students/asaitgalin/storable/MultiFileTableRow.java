package ru.fizteh.fivt.students.asaitgalin.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.asaitgalin.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MultiFileTableRow implements Storeable {
    private List<Class<?>> columnTypes = new ArrayList<>();
    private List<Object> columnData = new ArrayList<>();

    public MultiFileTableRow(List<Class<?>> columnTypes) {
        this.columnTypes = columnTypes;
        for (int i = 0; i < columnTypes.size(); ++i) {
            columnData.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (value != null) {
            checkClass(columnIndex, value.getClass());
        }
        columnData.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return columnData.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkClass(columnIndex, Integer.class);
        return (Integer)columnData.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkClass(columnIndex, Long.class);
        return (Long)columnData.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkClass(columnIndex, Byte.class);
        return (Byte)columnData.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkClass(columnIndex, Float.class);
        return (Float)columnData.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkClass(columnIndex, Double.class);
        return (Double)columnData.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkClass(columnIndex, Boolean.class);
        return (Boolean)columnData.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkClass(columnIndex, String.class);
        return (String)columnData.get(columnIndex);
    }

    public void setAllColumns(List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        for (int i = 0; i < values.size(); ++i) {
            setColumnAt(i, values.get(i));
        }
    }

    private void checkIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= columnTypes.size()) {
            throw new IndexOutOfBoundsException(String.format("storable: list index %d out of bounds", index));
        }
    }

    private void checkClass(int index, Class<?> cl) throws ColumnFormatException {
        if (!cl.isAssignableFrom(columnTypes.get(index))) {
            throw new ColumnFormatException(String.format("storable: invalid type %s for column %d, expected type %s",
                    cl.getName(), index, columnTypes.get(index).getName()));
        }
    }

    @Override
    public String toString() {
        return StringUtils.join(columnData, " ");
    }

    @Override
    public boolean equals(Object obj) {
        MultiFileTableRow row = (MultiFileTableRow)obj;
        return row.columnTypes.equals(columnTypes) && row.columnData.equals(columnData);
    }
}
