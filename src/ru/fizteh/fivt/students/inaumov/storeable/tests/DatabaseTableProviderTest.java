package ru.fizteh.fivt.students.inaumov.storeable.tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProviderFactory;

import java.io.IOException;

public class DatabaseTableProviderTest {
    TableProviderFactory tableProviderFactory = new DatabaseTableProviderFactory();

    @Test(expected = IllegalArgumentException.class)
    public void createDatabaseProviderEmptyDirShouldFail() {
        try {
            tableProviderFactory.create("");
        } catch (IOException e) {
            //
        }
    }
}
