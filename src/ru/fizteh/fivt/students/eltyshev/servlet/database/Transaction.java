package ru.fizteh.fivt.students.eltyshev.servlet.database;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.eltyshev.filemap.base.TransactionChanges;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTable;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProvider;

import java.io.IOException;
import java.text.ParseException;

public class Transaction {
    DatabaseTableProvider provider;
    DatabaseTable table;
    TransactionChanges localChanges;
    TransactionManager manager;
    TransactionChanges defaultTransaction;

    public String getTransactionId() {
        return transactionId;
    }

    String transactionId;

    public Transaction(DatabaseTableProvider provider, String tableName, TransactionManager manager) {
        this.provider = provider;
        this.manager = manager;
        table = (DatabaseTable) provider.getTable(tableName);
        defaultTransaction = table.getTransaction();
        localChanges = new TransactionChanges();
        localChanges.setStorage(table);
        transactionId = manager.makeTransactionId();
    }

    public int commit() throws IOException {
        table.setTransaction(localChanges);
        int result = table.commit();
        table.setTransaction(defaultTransaction);
        manager.endTransaction(transactionId);
        return result;
    }

    public int rollback() throws IOException {
        table.setTransaction(localChanges);
        int result = table.rollback();
        table.setTransaction(defaultTransaction);
        manager.endTransaction(transactionId);
        return result;
    }

    public String get(String key) {
        table.setTransaction(localChanges);
        try {
            Storeable value = table.get(key);
            if (value == null) {
                throw new IllegalArgumentException("key not found");
            }
            return provider.serialize(table, value);
        } finally {
            table.setTransaction(defaultTransaction);
        }
    }

    public String put(String key, String value) throws IOException {
        table.setTransaction(localChanges);
        try {
            Storeable oldValue = table.put(key, provider.deserialize(table, value));
            if (oldValue == null) {
                throw new IllegalArgumentException("key not found");
            }
            return provider.serialize(table, oldValue);
        } catch (ParseException e) {
            throw new IOException(e);
        } finally {
            table.setTransaction(defaultTransaction);
        }
    }

    public int size() {
        table.setTransaction(localChanges);
        try {
            return table.size();
        } finally {
            table.setTransaction(defaultTransaction);
        }
    }
}
