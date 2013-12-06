package ru.fizteh.fivt.students.vyatkina.database.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.vyatkina.database.superior.Type;

import java.util.List;

public class StorableRowShape {

    private List<Class<?>> classes;

    public StorableRowShape(List<Class<?>> classes) {
        if (classes == null || classes.size() == 0) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i) == null || Type.BY_CLASS.get(classes.get(i)) == null) {
                throw new ColumnFormatException("Bad columns");
            }
        }
        this.classes = classes;

    }

    public void columnFormatCheck(int columnIndex, Class<?> type) {
        indexInBoundsCheck(columnIndex);
        if (!type.equals(classes.get(columnIndex))) {
            throw new ColumnFormatException("Expected " + classes.get(columnIndex) + " in column " + columnIndex + " but found " + type);
        }
    }

    public void indexInBoundsCheck(int columnIndex) throws IndexOutOfBoundsException {
        if (!((columnIndex >= 0) && (columnIndex < classes.size()))) {
            throw new IndexOutOfBoundsException();
        }
    }


    public int getColumnsCount() {
        return classes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        indexInBoundsCheck(columnIndex);
        return classes.get(columnIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StorableRowShape shape = (StorableRowShape) o;

        if (classes != null ? !classes.equals(shape.classes) : shape.classes != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return classes != null ? classes.hashCode() : 0;
    }

}
