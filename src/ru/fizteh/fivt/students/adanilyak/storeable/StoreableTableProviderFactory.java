package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.adanilyak.tools.WorkStatus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 16:50
 */
public class StoreableTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private Map<String, StoreableTableProvider> allProvidersMap;
    private final Lock lock;
    WorkStatus status;

    public StoreableTableProviderFactory() {
        status = WorkStatus.WORKING;
        allProvidersMap = new HashMap<>();
        lock = new ReentrantLock(true);
    }

    @Override
    public TableProvider create(String directoryWithTables) throws IOException {
        status.isOkForOperations();
        if (directoryWithTables == null || directoryWithTables.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory not set or set incorrectly");
        }
        File file = new File(directoryWithTables);
        try {
            file = file.getCanonicalFile();
        } catch (IOException exc) {
            throw new IllegalArgumentException("Invalid directory", exc);
        }
        String directory = file.getAbsolutePath();
        lock.lock();
        try {
            if (!allProvidersMap.containsKey(directory)) {
                allProvidersMap.put(directory, new StoreableTableProvider(file));
            } else {
                if (!allProvidersMap.get(directory).isOkForOperations()) {
                    allProvidersMap.put(directory, new StoreableTableProvider(file));
                }
            }
            return allProvidersMap.get(directory);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        status.isOkForClose();
        lock.lock();
        try {
            for (StoreableTableProvider provider : allProvidersMap.values()) {
                provider.close();
            }
            status = WorkStatus.CLOSED;
        } finally {
            lock.unlock();
        }
    }
}
