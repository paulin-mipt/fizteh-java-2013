package ru.fizteh.fivt.students.eltyshev.parallel.tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProviderFactory;

import java.io.IOException;

public class ThreadSafeTableProviderFactoryTest {
    private final static int THREADS_COUNT = 5;

    @Test
    public void testCreateProvider() throws Exception {
        for (int threadNumber = 0; threadNumber < THREADS_COUNT; ++threadNumber) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    createProvider();
                }
            });
            thread.start();
        }
        createProvider();
    }

    private void createProvider() {
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        try {
            factory.create("C:\\temp\\storeable_test");
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
