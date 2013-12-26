package ru.fizteh.fivt.students.paulinMatavina.servlet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import ru.fizteh.fivt.students.paulinMatavina.filemap.*;
import ru.fizteh.fivt.storage.structured.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {
    private static final int MAX_TRANSNUM = 100000;
    private MyTableProvider tableProvider;
    private HashMap<String, MyTable> transactionMap;
    private HashMap<String, HashMap<String, Storeable>> transaction2Change;
    private final NumberFormat format = new DecimalFormat("00000");
    private HashMap<String, Storeable> savedChanges;
    private HashMap<String, Storeable> currentChanges;
    
    private ReentrantReadWriteLock mapsAccessLock;
    public class Transaction {
        private MyTable table;
        public Transaction(HashMap<String, Storeable> newChanges, MyTable newTable) {
            currentChanges = newChanges;
            table = newTable;
        }
        
        public void start() {
            savedChanges = table.getChanges();
            table.setChanges(currentChanges);
        }
        public void end() {
            table.setChanges(savedChanges);
        }
        
        public MyTable getTable() {
            return table;
        }
    }

    public Database(MyTableProvider prov) {
        tableProvider = prov;
        transactionMap = new HashMap<String, MyTable>();
        mapsAccessLock = new ReentrantReadWriteLock();
        transaction2Change = new HashMap<>();
    }

    public Transaction getTransaction(String name) {
        mapsAccessLock.readLock().lock();
        try {
            if (transactionMap.containsKey(name)) {
                HashMap<String, Storeable> change = transaction2Change.get(name);
                MyTable transTable = transactionMap.get(name);
                return new Transaction(change, transTable);
            } else {
                return null;
            }
        } finally {
            mapsAccessLock.readLock().unlock();
        }
    }

    public void cancelTransaction(String name) {
        mapsAccessLock.writeLock().lock();
        try {
            transactionMap.remove(name);
            transaction2Change.remove(name);
        } finally {
            mapsAccessLock.writeLock().unlock();
        }
    }

    public String makeNewID(String tableName) {
        mapsAccessLock.writeLock().lock();
        try {
            for (int tid = 0; tid < MAX_TRANSNUM; ++tid) {
                String tidString = format.format(tid);
                if (!transactionMap.containsKey(tidString)) {
                    MyTable table = (MyTable) tableProvider.getTable(tableName);
                    transactionMap.put(tidString, table);
                    transaction2Change.put(tidString, new HashMap<String, Storeable>());
                    return tidString;
                }
            }
            return null;
        } finally {
            mapsAccessLock.writeLock().unlock();
        }
    }
}
