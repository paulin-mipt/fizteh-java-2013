package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;
import java.text.ParseException;

public class Transaction {
    DatabaseTableProvider provider;
    DatabaseTable table;
    long tableTransactionId;
    TransactionWorker worker;

    public String getId() {
        return transactionId;
    }

    String transactionId;

    public Transaction(DatabaseTableProvider provider, String name, TransactionWorker worker) {
        this.provider = provider;
        this.worker = worker;
        table = (DatabaseTable) provider.getTable(name);
        tableTransactionId = TransactionPool.getInstance().createTransaction();
        TransactionPool.getInstance().getTransaction(tableTransactionId).defineStorage(table);
        transactionId = worker.makeId();
    }

    public int commit() throws IOException {
        int diff = table.commit(tableTransactionId);
        worker.stopTransaction(transactionId);
        return diff;
    }

    public int rollback() throws IOException {
        int diff = table.rollback(tableTransactionId);
        worker.stopTransaction(transactionId);
        return diff;
    }

    public String get(String key) {
        Storeable value = table.get(key, tableTransactionId);
        if (value == null) {
            throw new IllegalArgumentException("key not found");
        }
        return provider.serialize(table, value);
    }

    public String put(String key, String value) throws IOException {
        try {
            Storeable oldValue = table.put(key, provider.deserialize(table, value), tableTransactionId);
            if (oldValue == null) {
                throw new IllegalArgumentException("key not found");
            }
            return provider.serialize(table, oldValue);
        } catch (ParseException e) {
            throw new IOException("Error with serialization");
        }
    }

    public int size() {
        return table.size(tableTransactionId);
    }
}
