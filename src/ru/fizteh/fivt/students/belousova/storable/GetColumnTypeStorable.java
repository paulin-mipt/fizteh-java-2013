package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.Storeable;

public interface GetColumnTypeStorable extends Storeable {
    Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException;
}
