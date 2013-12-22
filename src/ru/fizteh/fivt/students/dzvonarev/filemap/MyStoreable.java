package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;
import java.util.List;

public class MyStoreable implements Storeable {

    public MyStoreable(Table table, List<?> args) throws IndexOutOfBoundsException, ColumnFormatException {
        columnTypes = new ArrayList<>();
        column = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columnTypes.add(table.getColumnType(i));
        }
        if (args.size() != columnTypes.size()) {            // wrong count of columns
            throw new IndexOutOfBoundsException("wrong type (wrong count of columns in value - " + args.size() + ")");
        }
        for (int i = 0; i < args.size(); ++i) {
            if (!myCastTo(columnTypes.get(i), args.get(i))) {
                throw new ColumnFormatException("wrong type (" + i + " column got wrong type - " + args.get(i) + ")");
            }
        }
    }

    public MyStoreable(Table table) {
        column = new ArrayList<>();
        columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columnTypes.add(table.getColumnType(i));
            column.add(null);
        }
    }

    private ArrayList<Object> column;                        // values in columns
    private ArrayList<Class<?>> columnTypes;                 // types of these values

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (value == null || value.equals(null)) {
            column.set(columnIndex, null);
            return;
        }
        Parser myParser = new Parser();
        if (!myParser.canBeCastedTo(columnTypes.get(columnIndex), value)) {
            throw new ColumnFormatException("wrong type (value " + value
                    + " got invalid type in " + columnIndex + " column)");
        }
        column.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        return column.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (!column.get(columnIndex).getClass().equals(Integer.class)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Integer) column.get(columnIndex);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (!column.get(columnIndex).getClass().equals(Long.class)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Long) column.get(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (!column.get(columnIndex).getClass().equals(Byte.class)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Byte) column.get(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (!column.get(columnIndex).getClass().equals(Float.class)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Float) column.get(columnIndex);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (!column.get(columnIndex).getClass().equals(Double.class)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Double) column.get(columnIndex);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (!column.get(columnIndex).getClass().equals(Boolean.class)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Boolean) column.get(columnIndex);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (!column.get(columnIndex).getClass().equals(String.class)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (String) column.get(columnIndex);
    }

    public boolean myCastTo(Class<?> type, Object obj) {
        if (obj == null || obj.equals(null)) {
            column.add(null);
            return true;
        }
        if (obj.getClass().equals(Integer.class)) {
            if (type.equals(Byte.class)) {
                Integer num = (Integer) obj;
                column.add(Integer.class.cast(obj).byteValue());
                return num >= -128 && num <= 127;
            }
            if (type.equals(Integer.class)) {
                column.add(Integer.class.cast(obj));
            }
            if (type.equals(Long.class)) {
                column.add(Integer.class.cast(obj).longValue());
            }
            if (type.equals(Double.class)) {
                column.add(Integer.class.cast(obj).doubleValue());
            }
            if (type.equals(Float.class)) {
                column.add(Integer.class.cast(obj).floatValue());
            }
            return type.equals(Integer.class) || type.equals(Long.class) || type.equals(Double.class)
                    || type.equals(Float.class);
        }
        if (obj.getClass().equals(Long.class)) {
            if (type.equals(Long.class)) {
                column.add(Long.class.cast(obj));
            }
            if (type.equals(Double.class)) {
                column.add(Long.class.cast(obj).doubleValue());
            }
            return type.equals(Long.class) || type.equals(Double.class);
        }
        if (obj.getClass().equals(Boolean.class)) {
            column.add(Boolean.class.cast(obj));
            return type.equals(Boolean.class);
        }
        if (obj.getClass().equals(String.class)) {
            column.add(String.class.cast(obj));
            return type.equals(String.class);
        }
        if (obj.getClass().equals(Byte.class)) {
            if (type.equals(Byte.class)) {
                column.add(Byte.class.cast(obj));
            }
            if (type.equals(Integer.class)) {
                column.add(Byte.class.cast(obj).intValue());
            }
            if (type.equals(Long.class)) {
                column.add(Byte.class.cast(obj).longValue());
            }
            if (type.equals(Double.class)) {
                column.add(Byte.class.cast(obj).doubleValue());
            }
            if (type.equals(Float.class)) {
                column.add(Byte.class.cast(obj).floatValue());
            }
            return !type.equals(String.class) && !type.equals(Boolean.class);
        }
        if (obj.getClass().equals(Float.class)) {
            if (type.equals(Double.class)) {
                column.add(Float.class.cast(obj).doubleValue());
            }
            if (type.equals(Float.class)) {
                column.add(Float.class.cast(obj));
            }
            return type.equals(Double.class) || type.equals(Float.class);
        }
        if (type.equals(Double.class)) {                                // for obj class double
            column.add(Double.class.cast(obj));
        }
        if (type.equals(Float.class)) {
            column.add(Double.class.cast(obj).floatValue());
        }
        return obj.getClass().equals(Double.class) && (type.equals(Double.class) || type.equals(Float.class));
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(MyStoreable.class.getSimpleName()).append("[");
        for (int i = 0; i < column.size(); ++i) {
            if (column.get(i) == null) {
                strBuilder.append("");
            } else {
                strBuilder.append(column.get(i).toString());
            }
            if (i != column.size() - 1) {
                strBuilder.append(",");
            }
        }
        strBuilder.append("]");
        return strBuilder.toString();
    }

}
