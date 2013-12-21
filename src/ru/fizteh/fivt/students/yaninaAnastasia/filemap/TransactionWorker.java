package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.util.HashMap;
import java.util.Map;

public class TransactionWorker {
    Map<String, Transaction> transactions = new HashMap<String, Transaction>();
    DatabaseTableProvider provider;

    int counter = 0;

    public TransactionWorker(DatabaseTableProvider provider) {
        this.provider = provider;
    }

    public String startTransaction(String name) {
        Transaction transaction = new Transaction(provider, name, this);
        transactions.put(transaction.getId(), transaction);
        return transaction.getId();
    }

    public Transaction getTransaction(String id) {
        return transactions.get(id);
    }

    void stopTransaction(String id) {
        transactions.remove(id);
    }

    String makeId() {
        StringBuilder builder = new StringBuilder();
        builder.append(counter);
        while (builder.length() < 5) {
            builder.insert(0, 0);
        }
        counter += 1;
        return builder.toString();
    }
}
