package ru.fizteh.fivt.students.valentinbarishev.proxy.test;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.proxy.MyLoggingProxyFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InvocationHandlerTest {
    static Table table;
    static TableProviderFactory factory;
    static TableProvider provider;
    static List<Class<?>> types;
    static MyLoggingProxyFactory logFactory;
    public StringWriter writer;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
        logFactory = new MyLoggingProxyFactory();
    }

    @Before
    public void before() {
        writer = new StringWriter();
    }

    @Test
    public void testFactory() throws IOException {
        TableProviderFactory proxyFactory = (TableProviderFactory) logFactory.wrap(writer,
                factory, TableProviderFactory.class);
        Assert.assertNotNull(proxyFactory);

        provider = proxyFactory.create(folder.newFolder("folder").getCanonicalPath());
        Assert.assertNotNull(provider);

        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        table = provider.createTable("simple", types);
        writer.flush();
    }

    @Test
    public void testProvider() throws IOException {
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());

        TableProvider proxyProvider = (TableProvider) logFactory.wrap(writer, provider, TableProvider.class);

        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        table = proxyProvider.createTable("simple", types);
        table = proxyProvider.getTable("simple");

        proxyProvider.createFor(table);

        writer.flush();
    }

    @Test
    public void testProviderError() throws IOException {
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());

        TableProvider proxyProvider = (TableProvider) logFactory.wrap(writer, provider, TableProvider.class);

        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        try {
            table = proxyProvider.createTable("", types);
        } catch (IllegalArgumentException e) {
            //silent
        }
        writer.flush();
    }


    @Test
    public void testTestInterface() throws IOException {
        List<Object> list = new ArrayList<>();
        list.add(list);
        list.add(new Integer(123));
        TestInterface test = (TestInterface) logFactory.wrap(writer, new TestClass(), TestInterface.class);

        test.arrayParameter(list);
        test.voidNoArgs();
        try {
            test.exceptionThrow();
        } catch (IOException e) {
            //silent
        }
        List<Object> list2 = new ArrayList<>();
        list2.add(list);
        list2.add(null);
        list2.add(list2);

        test.arrayParameter(list2);

        writer.flush();
    }

    @Test
    public void testNested() throws IOException {
        List<Object> array = new ArrayList<>();
        array.add(null);
        ArrayList<Object> nestedList = new ArrayList<>();
        array.add(nestedList);
        nestedList.add(array);

        TestInterface test = (TestInterface) logFactory.wrap(writer, new TestClass(), TestInterface.class);

        test.arrayParameter(array);

        writer.flush();
    }


    @Test
    public void proxyOfDbTypes() throws IOException {
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());

        TableProvider proxyProvider = (TableProvider) logFactory.wrap(writer, provider, TableProvider.class);

        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        table = proxyProvider.createTable("simple", types);
        table = proxyProvider.getTable("simple");

        Storeable storeable = (Storeable) logFactory.wrap(writer, proxyProvider.createFor(table), Storeable.class);
        storeable.setColumnAt(0, "123");
        storeable.setColumnAt(1, 123);

        Table proxyTable = (Table) logFactory.wrap(writer, table, Table.class);
        proxyTable.put("123", storeable);

        writer.flush();
    }
}
