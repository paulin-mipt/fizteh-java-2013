package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionPool {
    private static TransactionPool ourInstance = new TransactionPool();
    private Map<Long, TransactionWithModifies> pool = new HashMap<>();
    private long counter = 0;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public static TransactionPool getInstance() {
        return ourInstance;
    }

    private TransactionPool() {
    }

    public TransactionWithModifies getTransaction(Long id) {
        lock.readLock().lock();
        try {
            return pool.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public long createTransaction() {
        lock.writeLock().lock();
        try {
            long id = counter++;
            pool.put(id, new TransactionWithModifies());
            return id;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
