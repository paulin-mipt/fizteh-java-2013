package ru.fizteh.fivt.students.kislenko.parallels.test;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kislenko.junit.test.Cleaner;
import ru.fizteh.fivt.students.kislenko.storeable.MyTable;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProvider;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProviderFactory;
import ru.fizteh.fivt.students.kislenko.storeable.Value;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MyTableTest {
    private static MyTableProvider provider;
    private static File databaseDir = new File("database");
    private static ArrayList<Class<?>> typeList = new ArrayList<Class<?>>();
    private static MyTable table;

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
        table.clear();
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
        Assert.assertEquals("[\"bottom\",100500]",
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
        Assert.assertEquals("[\"bottom\",100500]", provider.serialize(table, table.remove("a")));
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
        Assert.assertEquals("[\"c\",2]", provider.serialize(table, table.get("РусскиеБуковкиТожеПоддерживаются")));
    }

    @Test
    public void testGetOverwritten() throws Exception {
        Value b = (Value) provider.deserialize(table, "[\"b\",1]");
        Value c = (Value) provider.deserialize(table, "[\"c\",2]");
        table.put("a", b);
        table.put("a", c);
        Assert.assertEquals("[\"c\",2]", provider.serialize(table, table.get("a")));
    }

    @Test
    public void testGetRemoved() throws Exception {
        Value b = (Value) provider.deserialize(table, "[\"b\",1]");
        Value d = (Value) provider.deserialize(table, "[\"d\",3]");
        table.put("a", b);
        table.put("c", d);
        Assert.assertEquals("[\"d\",3]", provider.serialize(table, table.get("c")));
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
        Assert.assertEquals("[\"b\",1]", provider.serialize(table, table.get("a")));
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
    public void testNonOp() throws Exception {
        Value a = (Value) provider.deserialize(table, "[\"nonOp\",0]");
        Value b = (Value) provider.deserialize(table, "[\"nonOp\",1]");
        table.put("key", a);
        table.commit();
        table.put("key", a);
        table.put("key", b);
        Assert.assertEquals(1, table.commit());
        table.put("key", b);
        table.remove("key");
        Assert.assertEquals(1, table.commit());
        table.put("key", a);
        Assert.assertEquals(1, table.commit());
        table.remove("key");
        Assert.assertEquals(1, table.commit());
        table.remove("key");
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void testMultiThreadPut() throws Exception {
        final AtomicReference<Storeable> ref1 = new AtomicReference<Storeable>();
        final AtomicReference<Storeable> ref2 = new AtomicReference<Storeable>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ref1.set(table.put("a", provider.deserialize(table, "[\"I gonna take this world!\",-1]")));
                } catch (ParseException ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ref2.set(table.put("a", provider.deserialize(table, "[\"I gonna take this world!\",-1]")));
                } catch (ParseException ignored) {
                    // This block isn't empty.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertNull(ref1.get());
        Assert.assertNull(ref1.get());
    }

    @Test
    public void testMultiThreadPutCommit() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        final AtomicReference<Integer> ref2 = new AtomicReference<Integer>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("a", provider.deserialize(table, "[\"string\",-1]"));
                    Thread.sleep(50);
                    ref1.set(table.commit());
                } catch (Exception ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("a", provider.deserialize(table, "[\"string\",-1]"));
                    Thread.sleep(50);
                    ref2.set(table.commit());
                } catch (Exception ignored) {
                    // This block isn't empty.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(1, ref1.get() + ref2.get());
    }

    @Test
    public void testMultiThreadPutSize() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        final AtomicReference<Integer> ref2 = new AtomicReference<Integer>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("a", provider.deserialize(table, "[\"empty\",-1]"));
                    ref1.set(table.size());
                } catch (ParseException ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("b", provider.deserialize(table, "[\"doubleEmpty\",-1]"));
                    table.put("c", provider.deserialize(table, "[\"tripleEmpty\",-1]"));
                    ref2.set(table.size());
                } catch (ParseException ignored) {
                    // This block isn't empty.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(1, ref1.get().intValue());
        Assert.assertEquals(2, ref2.get().intValue());
    }

    @Test
    public void testMultiThreadPutCommitSecond() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        final AtomicReference<Integer> ref2 = new AtomicReference<Integer>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("a", provider.deserialize(table, "[\"empty\",-1]"));
                    ref1.set(table.commit());
                } catch (ParseException ignored) {
                    // This block isn't empty.
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("a", provider.deserialize(table, "[\"empty\",-1]"));
                    table.put("b", provider.deserialize(table, "[\"pustovoytov\",-2]"));
                    ref2.set(table.commit());
                } catch (ParseException ignored) {
                    // This block isn't empty.
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertTrue((ref1.get() == 1 && ref2.get() == 1) || (ref1.get() == 0 && ref2.get() == 2));
    }

    @Test
    public void testMultiThreadPutCommitRollbackGet() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        final AtomicReference<Integer> ref2 = new AtomicReference<Integer>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("a", provider.deserialize(table, "[\"string\",-1]"));
                    Thread.sleep(50);
                    ref1.set(table.commit());
                } catch (Exception ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("a", provider.deserialize(table, "[\"string\",-1]"));
                    Thread.sleep(200);
                    ref2.set(table.rollback());
                } catch (Exception ignored) {
                    // This block isn't empty.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(1, ref1.get().intValue());
        Assert.assertEquals(0, ref2.get().intValue());
    }

    @Test
    public void testMultiThreadPutPutCommit() throws Exception {
        final AtomicReference<Integer> ref1 = new AtomicReference<Integer>();
        table.put("a", provider.deserialize(table, "[\"string\",-1]"));
        table.commit();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("a", provider.deserialize(table, "[\"string\",-1]"));
                    Thread.sleep(200);
                    ref1.set(table.commit());
                } catch (Exception ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                    table.put("a", provider.deserialize(table, "[\"string2\",-2]"));
                    table.commit();
                } catch (Exception ignored) {
                    // This block isn't empty.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(1, ref1.get().intValue());
    }
}
