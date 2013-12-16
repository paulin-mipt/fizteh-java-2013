package ru.fizteh.fivt.students.eltyshev.servlet.database;

import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProvider;

import java.util.HashMap;
import java.util.Map;

public class TransactionManager {

    Map<String, Transaction> transactions = new HashMap<String, Transaction>();
    DatabaseTableProvider provider;

    int transactionCounter = 0;

    public TransactionManager(DatabaseTableProvider provider) {
        this.provider = provider;
    }

    public String beginTransaction(String tableName) {
        Transaction transaction = new Transaction(provider, tableName, this);
        transactions.put(transaction.getTransactionId(), transaction);
        return transaction.getTransactionId();
    }

    public Transaction getTransaction(String transactionId) {
        return transactions.get(transactionId);
    }

    void endTransaction(String transactionId) {
        transactions.remove(transactionId);
    }

    String makeTransactionId() {
        StringBuilder sb = new StringBuilder();
        sb.append(transactionCounter);
        while (sb.length() < 5) {
            sb.insert(0, 0);
        }
        transactionCounter += 1;
        return sb.toString();
    }
}
