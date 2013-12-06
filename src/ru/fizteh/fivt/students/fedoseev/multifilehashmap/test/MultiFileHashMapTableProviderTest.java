package ru.fizteh.fivt.students.fedoseev.multifilehashmap.test;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTable;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTableProvider;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableProviderTest {
    private static MultiFileHashMapTableProvider tp;
    private File dbDir;

    public MultiFileHashMapTableProviderTest() throws IOException {
        dbDir = new File("test");

        dbDir.mkdirs();

        tp = new MultiFileHashMapTableProviderFactory().create(dbDir.getCanonicalFile().toString());
    }

    @Test
    public void testGetTable() throws Exception {
        MultiFileHashMapTable table1 = tp.createTable("trueStories");
        Assert.assertEquals(table1, tp.getTable("trueStories"));

        MultiFileHashMapTable table2 = tp.createTable("greatExpectations");
        Assert.assertEquals(table2, tp.getTable("greatExpectations"));

        Assert.assertEquals(table1, tp.getTable("trueStories"));
        Assert.assertEquals(table2, tp.getTable("greatExpectations"));

        tp.removeTable("trueStories");
        tp.removeTable("greatExpectations");
    }

    @Test
    public void testCreateTable() throws Exception {
        Assert.assertNotNull(tp.createTable("lower"));

        tp.removeTable("lower");
    }

    @Test
    public void testRemoveTable() throws Exception {
        tp.createTable("madmen");
        tp.createTable("lifeIsPain");
        tp.createTable("loony");

        tp.removeTable("madmen");
        Assert.assertNull(tp.getTable("madmen"));

        tp.removeTable("loony");
        Assert.assertNull(tp.getTable("loony"));

        Assert.assertNotNull(tp.getTable("lifeIsPain"));

        tp.removeTable("lifeIsPain");
    }

    @Test
    public void testCreateGetGetTable() throws Exception {
        MultiFileHashMapTable table = tp.createTable("bananas");
        MultiFileHashMapTable gotTable1 = tp.getTable("bananas");
        MultiFileHashMapTable gotTable2 = tp.getTable("bananas");


        Assert.assertSame(table, gotTable1);
        Assert.assertSame(gotTable1, gotTable2);

        tp.removeTable("bananas");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        tp.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmptyNameTable() throws Exception {
        tp.getTable("   \t   \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncorrectNameTable() throws Exception {
        tp.getTable("#$!U#JADE_UFremoval of the brainJHQ#R!342");
    }

    @Test
    public void testGetNotExistingNameTable() throws Exception {
        Assert.assertNull(tp.getTable("beHappy"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTable() throws Exception {
        tp.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyNameTable() throws Exception {
        tp.createTable("   \t   \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateIncorrectNameTable() throws Exception {
        tp.createTable("!@#$%^%&iesocremoval of the brainImq0114    qcxF!#EDЖД.бБЖДФБ");
    }

    @Test
    public void testCreateExistingNameTable() throws Exception {
        tp.createTable("shakeItOut");
        Assert.assertNull(tp.createTable("shakeItOut"));

        tp.removeTable("shakeItOut");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullTable() throws Exception {
        tp.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyNameTable() throws Exception {
        tp.removeTable("   \t   \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveIncorrectNameTable() throws Exception {
        tp.removeTable("w3waezfs3423!@!@e3qC2removal of the brainZАЫ СЯАЫАА!#?<>m^^");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingNameTable() throws Exception {
        tp.removeTable("doNotWorry");
    }

    @After
    public void tearDown() {
        dbDir.delete();
    }
}
