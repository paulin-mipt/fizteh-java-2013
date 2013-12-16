package ru.fizteh.fivt.students.eltyshev.filemap.base;

import ru.fizteh.fivt.students.eltyshev.multifilemap.DatabaseFileDescriptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractStorage<Key, Value> implements AutoCloseable {
    private final Lock transactionLock = new ReentrantLock(true);

    public static final Charset CHARSET = StandardCharsets.UTF_8;
    // Data
    protected HashMap<Key, Value> oldData;
    protected final ThreadLocal<TransactionChanges<Key, Value>> transaction = new ThreadLocal<TransactionChanges<Key, Value>>() {
        @Override
        public TransactionChanges<Key, Value> initialValue() {
            return new TransactionChanges<>();
        }
    };

    final protected String tableName;
    private String directory;
    protected ContainerState state;

    // Strategy
    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;

    public void setTransaction(TransactionChanges transaction) {
        this.transaction.set(transaction);
    }

    public TransactionChanges getTransaction() {
        return this.transaction.get();
    }

    // Constructor
    public AbstractStorage(String directory, String tableName) {
        this.directory = directory;
        this.tableName = tableName;
        oldData = new HashMap<Key, Value>();
        state = ContainerState.NOT_INITIALIZED;
        try {
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid file format");
        }
        transaction.get().setStorage(this);
        state = ContainerState.WORKING;
    }

    public int getUncommittedChangesCount() {
        return transaction.get().getUncommittedChanges();
    }

    // Table implementation
    public String getName() {
        state.checkOperationsAllowed();
        return tableName;
    }

    public Value storageGet(Key key) throws IllegalArgumentException {
        state.checkOperationsAllowed();

        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        return transaction.get().getValue(key);
    }

    public Value storagePut(Key key, Value value) throws IllegalArgumentException {
        state.checkOperationsAllowed();

        if (key == null || value == null) {
            String message = key == null ? "key " : "value ";
            throw new IllegalArgumentException(message + "cannot be null");
        }

        Value oldValue = transaction.get().getValue(key);

        transaction.get().addChange(key, value);
        return oldValue;
    }

    public Value storageRemove(Key key) throws IllegalArgumentException {
        state.checkOperationsAllowed();

        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (storageGet(key) == null) {
            return null;
        }

        Value oldValue = transaction.get().getValue(key);
        transaction.get().addChange(key, null);
        return oldValue;
    }

    public int storageSize() {
        state.checkOperationsAllowed();

        return transaction.get().getSize();
    }

    public int storageCommit() {
        state.checkOperationsAllowed();

        try {
            transactionLock.lock();
            int recordsCommitted = transaction.get().applyChanges();
            transaction.get().clear();

            try {
                save();
            } catch (IOException e) {
                System.err.println("storageCommit: " + e.getMessage());
                return 0;
            }

            return recordsCommitted;
        } finally {
            transactionLock.unlock();
        }
    }

    public int storageRollback() {
        state.checkOperationsAllowed();

        int recordsDeleted = transaction.get().countChanges();
        transaction.get().clear();
        return recordsDeleted;
    }

    public String getDatabaseDirectory() {
        return directory;
    }

    public Set<DatabaseFileDescriptor> getChangedFiles() {
        return transaction.get().getChangedFiles();
    }

    protected abstract DatabaseFileDescriptor makeDescriptor(Key key);

    public void rawPut(Key key, Value value) {
        oldData.put(key, value);
    }

    public Value rawGet(Key key) {
        return oldData.get(key);
    }

    protected String rawGetName() {
        return tableName;
    }

    @Override
    public void close() throws Exception {
        //state.checkOperationsAllowed();
        if (state.equals(ContainerState.CLOSED)) {
            return;
        }
        storageRollback();
        state = ContainerState.CLOSED;
    }

    public boolean isClosed() {
        return state.equals(ContainerState.CLOSED);
    }

}
