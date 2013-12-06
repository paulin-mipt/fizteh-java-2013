package ru.fizteh.fivt.students.baldindima.junit.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.baldindima.junit.MyTableProviderFactory;

import java.io.IOException;

public class MyTableProviderFactoryTests {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCreateNotNull() throws IOException {
        TableProviderFactory factory = new MyTableProviderFactory();
        Assert.assertNotNull(factory.create(folder.newFolder("folder").getCanonicalPath()));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        TableProviderFactory factory = new MyTableProviderFactory();
        factory.create(null);
    }

    
}