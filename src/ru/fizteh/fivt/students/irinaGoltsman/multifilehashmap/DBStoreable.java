package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class DBStoreable implements Storeable {
    private List<Class<?>> columnTypes = new ArrayList<>();
    private List<Object> rowValues = new ArrayList<>();

    public DBStoreable(List<Class<?>> types) {
        columnTypes = types;
        for (int i = 0; i < columnTypes.size(); ++i) {
            rowValues.add(null);
        }
    }

    private void checkIndex(int index) throws IndexOutOfBoundsException {
        if (index >= columnTypes.size() || index < 0) {
            throw new IndexOutOfBoundsException("invalid column index: " + index);
        }
    }

    private void checkEqualityTypes(int columnIndex, Class<?> type) {
        String rightType = columnTypes.get(columnIndex).getSimpleName();
        if (!rightType.equals(type.getSimpleName())) {
            throw new ColumnFormatException("type mismatch");
        }
    }

    // Установить значение в колонку
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (value != null) {
            checkEqualityTypes(columnIndex, value.getClass());
        }
        rowValues.set(columnIndex, value);
    }

    // Возвращает значение из данной колонки, не приводя его к конкретному типу.
    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return rowValues.get(columnIndex);
    }

    // Возвращает значение из данной колонки, приведя его к Integer.
    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkEqualityTypes(columnIndex, Integer.class);
        return (Integer) rowValues.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkEqualityTypes(columnIndex, Long.class);
        return (Long) rowValues.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkEqualityTypes(columnIndex, Byte.class);
        return (Byte) rowValues.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkEqualityTypes(columnIndex, Float.class);
        return (Float) rowValues.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkEqualityTypes(columnIndex, Double.class);
        return (Double) rowValues.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkEqualityTypes(columnIndex, Boolean.class);
        return (Boolean) rowValues.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkEqualityTypes(columnIndex, String.class);
        return (String) rowValues.get(columnIndex);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object value : rowValues) {
            if (value != null) {
                stringBuilder.append(value.toString());
            }
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return this.getClass().getSimpleName() + "[" + stringBuilder.toString() + "]";
    }
}
