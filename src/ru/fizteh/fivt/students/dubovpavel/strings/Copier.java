package ru.fizteh.fivt.students.dubovpavel.strings;

public interface Copier<V> {
    V copy(V obj);

    boolean equal(V left, V right);
}
