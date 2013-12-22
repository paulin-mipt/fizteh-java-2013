package ru.fizteh.fivt.students.baldindima.junit.storeabletests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.baldindima.junit.MyTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyTableProviderTests {
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
    public void before() throws IOException{
        path = folder.newFolder().getCanonicalPath();
        provider = factory.create(path);
        Assert.assertNotNull(provider);
    }

    @Test(expected = RuntimeException.class)
    public void testSignature() {
        new File(path, "test").mkdirs();
        provider.getTable("test");
    }

    @Test(expected = RuntimeException.class)
    public void testSignatureEmpty() throws IOException {
        File file = new File(path, "test");
        file.mkdirs();
        new File(file, "signature.tsv").createNewFile();
        provider.getTable("test");
    }

   

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyTypes() throws IOException {
        List<Class<?>> types = new ArrayList<>();

        Table table = provider.createTable("simple", types);
    }
    @Test
    public void testRemove() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);
        types.add(Byte.class);

        Table table = provider.createTable("first", types);
        Assert.assertNotNull(table);
        Assert.assertNotNull(provider.getTable("first"));
        provider.removeTable("first");
        Assert.assertNull(provider.getTable("first"));
    }

}