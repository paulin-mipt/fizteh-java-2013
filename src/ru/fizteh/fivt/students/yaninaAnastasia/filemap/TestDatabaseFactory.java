package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class TestDatabaseFactory {
    String path;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void beforeTest() {
        path = folder.getRoot().getPath();
    }

    @Test
    public void testCreateNormalFactory() throws IOException {
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        Assert.assertNotNull(factory.create(new File(path).getCanonicalPath()));
    }
}
