package ru.fizteh.fivt.students.inaumov.storeable.tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProviderFactory;

import java.io.IOException;

public class DatabaseTableProviderFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void testCreateProviderNullDir() throws Exception {
        TableProviderFactory tableProviderFactory = new DatabaseTableProviderFactory();
        try {
            tableProviderFactory.create(null);
        } catch (IOException e) {
            //
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProviderEmptyDir() throws Exception {
        TableProviderFactory tableProviderFactory = new DatabaseTableProviderFactory();
        try {
            tableProviderFactory.create("");
        } catch (IOException e) {
            //
        }
    }
}
