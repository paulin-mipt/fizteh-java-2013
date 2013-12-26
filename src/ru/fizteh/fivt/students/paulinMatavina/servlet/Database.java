package ru.fizteh.fivt.students.paulinMatavina.servlet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.paulinMatavina.filemap.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("rawtypes")
public class Database {
    private static final int MAX_TRANSNUM = 100000;
    private MyTableProvider tableProvider;
    private HashMap<String, MyTable> transactionMap;
    private HashMap<String, Map[][]> transaction2Change;
    private final NumberFormat format = new DecimalFormat("00000");
    
    private ReentrantReadWriteLock mapsAccessLock;
    public class Transaction {
        private MyTable table;

        public Transaction(Map[][] newChanges, MyTable newTable) {
            table = newTable;
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
                Map[][] change = transaction2Change.get(name);
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
                    transaction2Change.put(tidString, new Map[MyTable.FOLDER_NUM][MyTable.FILE_IN_FOLD_NUM]);
                    return tidString;
                }
            }
            return null;
        } finally {
            mapsAccessLock.writeLock().unlock();
        }
    }
}
