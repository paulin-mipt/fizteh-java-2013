package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProviderFactory;
import org.junit.Test;

import java.io.IOException;

public class DBTableProviderFactoryTest {
    TableProviderFactory factory = new DBTableProviderFactory();

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateErrorName() throws Exception {
        factory.create("//\0");
    }

    @Test(expected = IllegalStateException.class)
    public void createAfterClosing() throws IOException {
        ((DBTableProviderFactory) factory).close();
        factory.create("rootDir");
    }
}
