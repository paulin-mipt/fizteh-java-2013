package ru.fizteh.fivt.students.dubovpavel.filemap;

public interface DataBaseAccessible<K, V> {
    DataBaseHandler<K, V> getDataBase();
}
