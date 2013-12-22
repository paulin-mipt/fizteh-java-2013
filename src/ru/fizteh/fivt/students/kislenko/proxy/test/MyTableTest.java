package ru.fizteh.fivt.students.kislenko.proxy.test;

import org.junit.*;
import ru.fizteh.fivt.students.kislenko.junit.test.Cleaner;
import ru.fizteh.fivt.students.kislenko.storeable.MyTable;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProvider;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProviderFactory;
import ru.fizteh.fivt.students.kislenko.storeable.Value;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

public class MyTableTest {
    private static MyTableProvider provider;
    private static File databaseDir = new File("database");
    private static ArrayList<Class<?>> typeList = new ArrayList<Class<?>>();
    MyTable table;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        databaseDir.mkdir();
        provider = factory.create("database");
        typeList.add(String.class);
        typeList.add(Long.class);
    }

    @Before
    public void setUp() throws Exception {
        table = provider.createTable("table", typeList);
    }

    @After
    public void tearDown() throws Exception {
        provider.removeTable("table");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Cleaner.clean(databaseDir);
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("table", table.getName());
    }

    @Test
    public void testPutNormalValue() throws Exception {
        Assert.assertNull(table.put("java", provider.deserialize(table, "[\"Komanov\",58486067788038353]")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        table.put("nullKey", null);
    }

    @Test(expected = ParseException.class)
    public void testPutBadValue() throws Exception {
        table.put("badValueKey", provider.deserialize(table, "[\"wtf\",\"lol\"]"));
    }

    @Test
    public void testGetNormalValue() throws Exception {
        table.put("key1", provider.deserialize(table, "[\"stringValue1\",1]"));
        Assert.assertEquals("[\"stringValue1\",1]", provider.serialize(table, table.get("key1")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        table.put(null, provider.deserialize(table, "[\"Stop do so stupid test!\",0]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutIncorrectKey() throws Exception {
        table.put("     ", provider.deserialize(table, "[\"В чём смысл жизни?\", 42]"));
    }

    @Test(expected = ParseException.class)
    public void testPutIncorrectValue() throws Exception {
        table.put("java", provider.deserialize(table, "тыр-тыр, ололо, я водитель НЛО"));
    }

    @Test(expected = ParseException.class)
    public void testPutValueWithIncorrectTypeList() throws Exception {
        table.put("someKey", provider.deserialize(table, "[0,0]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncorrectKey() throws Exception {
        table.get("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveIncorrectKey() throws Exception {
        table.remove("      ");
    }

    @Test
    public void testPutOverwrite() throws Exception {
        Value temp = (Value) provider.deserialize(table, "[\"bottom\",100500]");
        table.put("a", temp);
        Assert.assertEquals(provider.serialize(table, temp),
                provider.serialize(table, table.put("a", provider.deserialize(table, "[\"BOTTOM\",100500]"))));
    }

    @Test
    public void testRemoveNotExistingKey() throws Exception {
        Assert.assertNull(table.remove("someKey"));
    }

    @Test
    public void testRemoveSimple() throws Exception {
        Value temp = (Value) provider.deserialize(table, "[\"bottom\",100500]");
        table.put("a", temp);
        Assert.assertEquals(provider.serialize(table, temp), provider.serialize(table, table.remove("a")));
    }

    @Test
    public void testGetNotExistingKey() throws Exception {
        Assert.assertNull(table.get("nothing"));
    }

    @Test
    public void testGetSimple() throws Exception {
        Value b = (Value) provider.deserialize(table, "[\"b\",1]");
        table.put("a", b);
        Assert.assertEquals("[\"b\",1]", provider.serialize(table, table.get("a")));
        Value c = (Value) provider.deserialize(table, "[\"c\",2]");
        table.put("РусскиеБуковкиТожеПоддерживаются", c);
        Assert.assertEquals(provider.serialize(table, c),
                provider.serialize(table, table.get("РусскиеБуковкиТожеПоддерживаются")));
    }

    @Test
    public void testGetOverwritten() throws Exception {
        Value b = (Value) provider.deserialize(table, "[\"b\",1]");
        Value c = (Value) provider.deserialize(table, "[\"c\",2]");
        table.put("a", b);
        table.put("a", c);
        Assert.assertEquals(provider.serialize(table, c), provider.serialize(table, table.get("a")));
    }

    @Test
    public void testGetRemoved() throws Exception {
        Value b = (Value) provider.deserialize(table, "[\"b\",1]");
        Value d = (Value) provider.deserialize(table, "[\"d\",3]");
        table.put("a", b);
        table.put("c", d);
        Assert.assertEquals(provider.serialize(table, d), provider.serialize(table, table.get("c")));
        table.remove("c");
        Assert.assertNull(table.get("c"));
    }

    @Test
    public void testCommit() throws Exception {
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void testRollback() throws Exception {
        Assert.assertEquals(0, table.rollback());
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void testPutRollbackGet() throws Exception {
        Value useless = (Value) provider.deserialize(table, "[\"kreslo\",666]");
        table.put("useless", useless);
        table.rollback();
        Assert.assertNull(table.get("useless"));
    }

    @Test
    public void testPutCommitGet() throws Exception {
        Value b = (Value) provider.deserialize(table, "[\"b\",1]");
        table.put("a", b);
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals(provider.serialize(table, b), provider.serialize(table, table.get("a")));
    }

    @Test
    public void testPutCommitRemoveRollbackGet() throws Exception {
        Value veryImportantValue = (Value) provider.deserialize(table, "[\"importantValue\",65534]");
        table.put("useful", veryImportantValue);
        table.commit();
        table.remove("useful");
        table.rollback();
        Assert.assertEquals("[\"importantValue\",65534]", provider.serialize(table, table.get("useful")));
    }

    @Test
    public void testPutRemoveSize() throws Exception {
        Value b = (Value) provider.deserialize(table, "[\"b\",1]");
        Value c = (Value) provider.deserialize(table, "[\"c\",2]");
        table.put("a", b);
        table.put("b", c);
        table.remove("c");
        Assert.assertEquals(2, table.size());
        table.remove("b");
        Assert.assertEquals(1, table.size());
    }

    @Test
    public void testPutCommitRollbackSize() throws Exception {
        Value b = (Value) provider.deserialize(table, "[\"b\",1]");
        Value c = (Value) provider.deserialize(table, "[\"c\",2]");
        table.put("a", b);
        table.put("b", c);
        table.put("b", c);
        Assert.assertEquals(2, table.commit());
        Assert.assertEquals(2, table.size());
        table.remove("b");
        table.remove("a");
        Assert.assertEquals(0, table.size());
        Assert.assertEquals(2, table.rollback());
        Assert.assertEquals(2, table.size());
    }

    @Test
    public void testCloseGet() throws Exception {
        MyTable table2 = provider.createTable("tableForClose", typeList);
        table2.put("closingTableKey", provider.deserialize(table2, "[\"key\",512]"));
        table2.commit();
        table2.close();
        MyTable newTable = provider.getTable("tableForClose");
        newTable.put("closingTableKey2", provider.deserialize(newTable, "[\"key\",1024]"));
        Assert.assertEquals("[\"key\",512]", provider.serialize(newTable, newTable.get("closingTableKey")));
        Assert.assertEquals("[\"key\",1024]", provider.serialize(newTable, newTable.get("closingTableKey2")));
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("MyTable[" + provider.getPath().toAbsolutePath().resolve(table.getName()).toString() + "]",
                table.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testCommitAfterClose() throws Exception {
        table.close();
        table.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void testRollbackAfterClose() throws Exception {
        table.close();
        table.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void testSizeAfterClose() throws Exception {
        table.close();
        table.size();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAfterClose() throws Exception {
        table.put("lonelyKeyInClosedTable", provider.deserialize(table, "[\"\",924]"));
        table.close();
        table.get("lonelyKeyInClosedTable");
    }

    @Test(expected = IllegalStateException.class)
    public void testPutAfterClose() throws Exception {
        table.close();
        table.put("nothing", provider.deserialize(table, "[\"reallyNothing\",0]"));
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowingExceptionInPut() throws Exception {
        table.close();
        table.put("sumkin", provider.deserialize(table, "[\"molodets\",\"aga\",0,1,2,3,4,5,\"ololopwnz100500\"]"));
    }
}
