package ru.fizteh.fivt.students.valentinbarishev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyTableProviderTest {
    static TableProviderFactory factory;
    static TableProvider provider;
    static String path;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Before
    public void before() throws IOException {
        path = folder.newFolder().getCanonicalPath();
        provider = factory.create(path);
        Assert.assertNotNull(provider);
    }

    @Test(expected = Error.class)
    public void testSignature() {
        new File(path, "test").mkdirs();
        provider.getTable("test");
    }

    @Test(expected = Error.class)
    public void testSignatureEmpty() throws IOException {
        File file = new File(path, "test");
        file.mkdirs();
        new File(file, "signature.tsv").createNewFile();
        provider.getTable("test");
    }

    @Test
    public void testRemove() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        Table table = provider.createTable("simple", types);
        Assert.assertNotNull(table);
        Assert.assertNotNull(provider.getTable("simple"));
        provider.removeTable("simple");
        Assert.assertNull(provider.getTable("simple"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyTypes() throws IOException {
        List<Class<?>> types = new ArrayList<>();

        Table table = provider.createTable("simple", types);
    }

    @Test (expected = IllegalStateException.class)
    public void testCloseCreate() throws Exception {
        ((AutoCloseable) provider).close();
        provider.createTable("asdasd", new ArrayList<Class<?>>());
    }

    @Test (expected = IllegalStateException.class)
    public void testCloseGet() throws Exception {
        ((AutoCloseable) provider).close();
        provider.getTable("asdasd");
    }

    @Test (expected = IllegalStateException.class)
    public void testCloseRemove() throws Exception {
        ((AutoCloseable) provider).close();
        provider.removeTable("asdasd");
    }
}
