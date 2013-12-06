package ru.fizteh.fivt.students.valentinbarishev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyStoreableTest {
    static Table table;
    static TableProviderFactory factory;
    static TableProvider provider;
    static List<Class<?>> types;
    static Storeable s;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Before
    public void beforeTest() throws IOException {
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());
        types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        types.add(Byte.class);
        types.add(Long.class);
        types.add(Double.class);
        types.add(Float.class);
        types.add(Boolean.class);
        table = provider.createTable("simple", types);
        s = provider.createFor(table);
        s.setColumnAt(0, 1);
        s.setColumnAt(1, "1");
        s.setColumnAt(2, 1);
        s.setColumnAt(3, 1);
        s.setColumnAt(4, 1.11);
        s.setColumnAt(5, 1.2);
        s.setColumnAt(6, true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBoundsException() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(122, "12");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBoundsExceptionMore() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(-2, "44");
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatException() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(0, true);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatExceptionMore() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(0, "asdasd");
        storeable.getIntAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatExceptionLongInt() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(1, new Long("100000000000"));
        storeable.getIntAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatExceptionDouble() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(1, new Double(0.0001));
        storeable.getIntAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatExceptionFloat() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(1, new Float(0.0201));
        storeable.getIntAt(1);
    }

    @Test
    public void testSetByteAtIntColumn() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(0, (byte) 11);
        storeable.getIntAt(0);
    }

    @Test (expected = ColumnFormatException.class)
    public void testColumnFormatExceptionIntYo() {
        s.getIntAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void testColumnFormatExceptionByte() {
        s.getByteAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void testColumnFormatExceptionLong() {
        s.getLongAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void testColumnFormatExceptionDoubleYo() {
        s.getDoubleAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void testColumnFormatExceptionFloatYo() {
        s.getFloatAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void testColumnFormatExceptionString() {
        s.getStringAt(0);
    }

    @Test (expected = ColumnFormatException.class)
    public void testColumnFormatExceptionBoolean() {
        s.getBooleanAt(0);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsExceptionInt() {
        s.getIntAt(100);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsExceptionString() {
        s.getStringAt(100);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsExceptionByte() {
        s.getByteAt(100);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsExceptionLong() {
        s.getLongAt(100);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsExceptionDouble() {
        s.getDoubleAt(100);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsExceptionFloat() {
        s.getFloatAt(100);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsExceptionBoolean() {
        s.getBooleanAt(100);
    }
}
