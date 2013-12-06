package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container;

import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntryReader;
import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntryWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TableContainer<ValueType> {
    private static final int DIR_COUNT = 16;
    private static final int FILES_PER_DIR = 16;

    private class TransactionData {
        private Map<String, ValueType> currentTable;
        private int changesCount;

        public TransactionData() {
            this.currentTable = new HashMap<>();
            this.changesCount = 0;
        }

        public void transactionPut(String key, ValueType value) {
            currentTable.put(key, value);
        }

        public ValueType transactionGet(String key) {
            if (currentTable.containsKey(key)) {
                return currentTable.get(key);
            }
            return originalTable.get(key);
        }

        public int transactionCommit() {
            int count = 0;
            for (String key : currentTable.keySet()) {
                ValueType value = currentTable.get(key);
                if (transactionHasChanges(value, originalTable.get(key))) {
                    if (value == null) {
                        originalTable.remove(key);
                    } else {
                        originalTable.put(key, value);
                    }
                    ++count;
                }
            }
            return count;
        }

        public void transactionIncreaseChangesCount() {
            ++changesCount;
        }

        public int transactionGetChangesCount() {
            return changesCount;
        }

        public void transactionClearChanges() {
            currentTable.clear();
            changesCount = 0;
        }

        public int transactionGetSize() {
            return originalTable.size() + transactionCalcSize();
        }

        public int transactionsCalcChanges() {
            int count = 0;
            for (String key : currentTable.keySet()) {
                ValueType newValue = currentTable.get(key);
                if (transactionHasChanges(newValue, originalTable.get(key))) {
                    ++count;
                }
            }
            return count;
        }

        private int transactionCalcSize() {
            int count = 0;
            for (String key : currentTable.keySet()) {
                ValueType newValue = currentTable.get(key);
                ValueType oldValue = originalTable.get(key);
                if (newValue == null && oldValue != null) {
                    --count;
                } else if (newValue != null && oldValue == null) {
                    ++count;
                }
            }
            return count;
        }

        private boolean transactionHasChanges(ValueType oldValue, ValueType newValue) {
            if (newValue == null && oldValue == null) {
                return false;
            }
            if (newValue == null || oldValue == null) {
                return true;
            }
            return !oldValue.equals(newValue);
        }
    }

    private ThreadLocal<TransactionData> transactions;
    private Map<String, ValueType> originalTable;
    private TableValuePacker<ValueType> packer;
    private TableValueUnpacker<ValueType> unpacker;

    protected final Lock transactionsLock = new ReentrantLock(true);

    private File tableDirectory;

    public TableContainer(File tableDirectory, TableValuePacker<ValueType> packer, TableValueUnpacker<ValueType> unpacker) {
        this.transactions = new ThreadLocal<TransactionData>() {
            @Override
            protected TransactionData initialValue() {
                return new TransactionData();
            }
        };
        this.originalTable = new HashMap<>();
        this.tableDirectory = tableDirectory;
        this.packer = packer;
        this.unpacker = unpacker;
    }

    public ValueType containerGetValue(String key) {
        return transactions.get().transactionGet(key);
    }

    public ValueType containerPutValue(String key, ValueType value) {
        ValueType oldValue = transactions.get().transactionGet(key);
        transactions.get().transactionPut(key, value);
        return oldValue;
    }

    public ValueType containerRemoveValue(String key) {
        ValueType oldValue = transactions.get().transactionGet(key);
        transactions.get().transactionPut(key, null);
        transactions.get().transactionIncreaseChangesCount();
        return oldValue;
    }

    public int containerCommit() throws IOException {
        try {
            transactionsLock.lock();
            int changesCount = transactions.get().transactionCommit();
            transactions.get().transactionClearChanges();
            containerSave();
            return changesCount;
        } finally {
            transactionsLock.unlock();
        }
    }

    public int containerRollback() {
        int count = transactions.get().transactionsCalcChanges();
        transactions.get().transactionClearChanges();
        return count;
    }

    public void containerSave() throws IOException {
        for (int i = 0; i < DIR_COUNT; ++i) {
            for (int j = 0; j < FILES_PER_DIR; ++j) {
                Map<String, String> values = new HashMap<>();
                for (String s : originalTable.keySet()) {
                    if (getKeyDir(s) == i && getKeyFile(s) == j) {
                        try {
                            values.put(s, packer.getValueString(originalTable.get(s)));
                        } catch (Exception e) {
                            throw new IOException(e);
                        }
                    }
                }
                if (values.size() > 0) {
                    File keyDir = new File(tableDirectory, i + ".dir");
                    if (!keyDir.exists()) {
                        keyDir.mkdir();
                    }
                    File fileName = new File(keyDir, j + ".dat");
                    TableEntryWriter writer = new TableEntryWriter(fileName);
                    writer.writeEntries(values);
                }
            }
        }
    }

    public void containerLoad() throws IOException {
        for (File subDir : tableDirectory.listFiles()) {
            if (subDir.isDirectory()) {
                boolean hasFiles = false;
                for (File f : subDir.listFiles()) {
                    hasFiles = true;
                    TableEntryReader reader = new TableEntryReader(f);
                    while (reader.hasNextEntry()) {
                        Map.Entry<String, String> entry = reader.readNextEntry();
                        File validFile = new File(new File(tableDirectory, getKeyDir(entry.getKey()) + ".dir"),
                        getKeyFile(entry.getKey()) + ".dat");
                        if (!f.equals(validFile)) {
                            throw new IOException("Corrupted database");
                        }
                        try {
                            originalTable.put(entry.getKey(), unpacker.getValueFromString(entry.getValue()));
                        } catch (Exception e) {
                            throw new IOException(e);
                        }
                    }
                }
                if (!hasFiles) {
                    throw new IOException("empty dir");
                }
            }
        }
    }

    public int containerGetSize() {
        return transactions.get().transactionGetSize();
    }

    public int containerGetChangesCount() {
        return transactions.get().transactionGetChangesCount();
    }

    private int getKeyDir(String key) {
        return Math.abs(key.hashCode()) % DIR_COUNT;
    }

    private int getKeyFile(String key) {
        return Math.abs(key.hashCode()) / DIR_COUNT % FILES_PER_DIR;
    }

}
