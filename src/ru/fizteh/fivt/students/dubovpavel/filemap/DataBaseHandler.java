package ru.fizteh.fivt.students.dubovpavel.filemap;

public interface DataBaseHandler<K, V> {
    public class DataBaseException extends Exception {
        public final boolean acceptable;

        public DataBaseException(String msg) {
            super(msg);
            acceptable = true;
        }

        public DataBaseException(String msg, boolean acc) {
            super(msg);
            acceptable = acc;
        }
    }

    V put(K key, V value) throws DataBaseException;

    V get(K key) throws DataBaseException;

    void save() throws DataBaseException;

    void open() throws DataBaseException;

    V remove(K key) throws DataBaseException;
}
