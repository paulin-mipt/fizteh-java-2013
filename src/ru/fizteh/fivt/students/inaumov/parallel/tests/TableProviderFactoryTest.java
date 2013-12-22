package ru.fizteh.fivt.students.inaumov.parallel.tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProviderFactory;

import java.io.IOException;

public class TableProviderFactoryTest {
    private static final int THREADS_NUMBER = 10;
    private static final String DATABASE_DIRECTORY = "./parallel_test";

    @Test
    public void testCreateProvider() {
        for  (int i = 0; i < THREADS_NUMBER; ++i) {
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
        TableProviderFactory tableProviderFactory = new DatabaseTableProviderFactory();
        try {
            tableProviderFactory.create(DATABASE_DIRECTORY);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
