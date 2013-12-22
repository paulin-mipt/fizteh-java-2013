package ru.fizteh.fivt.students.elenarykunova.filemap.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.elenarykunova.filemap.*;

public class MyStoreableTest {

    private MyStoreable stor;
    
    @Rule 
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepare() {
        File rootDir;
        try {
            rootDir = folder.newFolder("myroot");
            MyTableProviderFactory factory = new MyTableProviderFactory();
            MyTableProvider prov = (MyTableProvider) factory.create(rootDir.getAbsolutePath());
            List<Class<?>> types = new ArrayList<Class<?>>(7);
            types.add(0, Integer.class);
            types.add(1, Double.class);
            types.add(2, String.class);
            types.add(3, Float.class);
            types.add(4, Byte.class);
            types.add(5, Boolean.class);
            types.add(6, Long.class);
            
            MyTable table = (MyTable) prov.createTable("newTable", types);
            stor = new MyStoreable(table);
        } catch (IOException e) {
            System.err.println("can't make tests");
        }
    }
   
    @Test (expected = IndexOutOfBoundsException.class)
    public void testSetColumnSmallIndex() {
        stor.setColumnAt(-1, "Aaa");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testSetColumnBigIndex() {
        stor.setColumnAt(10, "Aaa");
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIntColumnSmallIndex() {
        stor.getIntAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIntColumnBigIndex() {
        stor.getIntAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testFloatColumnSmallIndex() {
        stor.getFloatAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testFloatColumnBigIndex() {
        stor.getFloatAt(10);
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void testDoubleColumnSmallIndex() {
        stor.getDoubleAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testDoubleColumnBigIndex() {
        stor.getDoubleAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testByteColumnSmallIndex() {
        stor.getByteAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testByteColumnBigIndex() {
        stor.getByteAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testStringColumnSmallIndex() {
        stor.getStringAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testStringColumnBigIndex() {
        stor.getStringAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testLongColumnSmallIndex() {
        stor.getLongAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testLongColumnBigIndex() {
        stor.getLongAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testBooleanColumnSmallIndex() {
        stor.getBooleanAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testBooleanColumnBigIndex() {
        stor.getBooleanAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testGetColumnSmallIndex() {
        stor.getColumnAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testGetColumnBigIndex() {
        stor.getColumnAt(10);
    }

    @Test (expected = ColumnFormatException.class)
    public void testSetIntColumnInvalidType() {
        stor.setColumnAt(0, "Aaa");
    }

    @Test (expected = ColumnFormatException.class)
    public void testSetDoubleColumnInvalidType() {
        stor.setColumnAt(1, true);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void testSetStringColumnInvalidType() {
        stor.setColumnAt(2, 555);
    }

    @Test (expected = ColumnFormatException.class)
    public void testSetFloatColumnInvalidType() {
        stor.setColumnAt(3, false);
    }

    @Test (expected = ColumnFormatException.class)
    public void testSetByteColumnInvalidType() {
        stor.setColumnAt(4, 1000000);
    }

    @Test (expected = ColumnFormatException.class)
    public void testSetBooleanColumnInvalidType() {
        stor.setColumnAt(5, "azaza");
    }

    @Test (expected = ColumnFormatException.class)
    public void testSetLongColumnInvalidType() {
        stor.setColumnAt(6, 5.3333);
    }

    @Test
    public void testSetColumnAt() {
        stor.setColumnAt(0, 10);
        assertEquals((int) stor.getIntAt(0), 10);
        stor.setColumnAt(1, 1.2);
        stor.setColumnAt(2, "value");
        assertEquals((String) stor.getStringAt(2), "value");
    }
    
    @Test
    public void testGetColumnAt() {
        stor.setColumnAt(0, 10);
        stor.setColumnAt(1, 1.2);
        stor.setColumnAt(2, "value");
        assertEquals(stor.getColumnAt(0).getClass(), Integer.class);
        assertEquals(stor.getColumnAt(1).getClass(), Double.class);
        assertEquals(stor.getColumnAt(2).getClass(), String.class);
    }
    
    @Test
    public void testGetIntAt() {
        Integer i = 400;
        stor.setColumnAt(0, i);
        Integer res = stor.getIntAt(0);
        assertEquals(i, res);
    }

    @Test
    public void testGetLongAt() {
        Long l = Long.valueOf(1005000101);
        stor.setColumnAt(6, l);
        Long res = stor.getLongAt(6);
        assertEquals(l, res);
    }

    @Test
    public void testGetByteAt() {
        Byte b = 12;
        stor.setColumnAt(4, b);
        Byte res = stor.getByteAt(4);
        assertEquals(b, res);
    }

    @Test
    public void testGetFloatAt() {
        Float fl = (float) 1.3;
        stor.setColumnAt(3, fl);
        Float res = stor.getFloatAt(3);
        if (Math.abs(res - fl) > 0.00001) {
            fail("expected 1.3, but was " + res);
        }
    }

    @Test
    public void testGetDoubleAt() {
        Double db = 1.9399393;
        stor.setColumnAt(1, db);
        Double res = stor.getDoubleAt(1);
        if (Math.abs(res - db) > 0.00001) {
            fail("expected " + db + ", but was " + res);
        }
    }

    @Test
    public void testGetBooleanAt() {
        Boolean bool = true;
        stor.setColumnAt(5, bool);
        Boolean res = stor.getBooleanAt(5);
        assertEquals(bool,  res);
    }

    @Test
    public void testGetStringAt() {
        String str = "sdfd10gfg05  000101";
        stor.setColumnAt(2, str);
        String res = stor.getStringAt(2);
        assertEquals(str, (String) res);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void testSetStringInegerShouldFail() {
        int i = 42;
        stor.setColumnAt(2, i);
    }
    
    @Test
    public void toStringTest() {
        stor.setColumnAt(0, 10);
        stor.setColumnAt(1, 1.5);
        stor.setColumnAt(2, "string");
        stor.setColumnAt(3, null);
        stor.setColumnAt(4, null);
        stor.setColumnAt(5, true);
        stor.setColumnAt(6, 10L);
        assertEquals("MyStoreable[10,1.5,string,,,true,10]", stor.toString());
    }
}
