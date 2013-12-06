package ru.fizteh.fivt.students.drozdowsky.databaseTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.drozdowsky.commands.ShellController;
import ru.fizteh.fivt.students.drozdowsky.database.FileHashMap;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class FileHashMapTest {
    static FileHashMap table;
    static File databaseDir;

    @Before
    public void setUp() {
        String workingDir = System.getProperty("user.dir") + "/" + "test";
        while (new File(workingDir).exists()) {
            workingDir = workingDir + "1";
        }
        databaseDir = new File(workingDir);
        databaseDir.mkdir();
        table = new FileHashMap(databaseDir);
    }

    @Test
    public void getNameTest() {
        assertEquals(table.getName(), "test");
    }

    @Test
    public void noChangeCommitTest() {
        table.put("key", "value");
        table.remove("key");
        assertEquals(table.commit(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalKeyShouldFail() {
        table.put("bad key", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyValueShouldFail() {
        table.put("key", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullKeyShouldFail() {
        table.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullValueShouldFail() {
        table.put("key", null);
    }

    @Test
    public void rollbackAndCommitTest() {
        table.put("key1", "value 1");
        table.commit();
        table.put("key1", "value 2");
        table.put("key2", "value 3");
        table.remove("key3");
        assertEquals(table.rollback(), 2);
        assertEquals(table.get("key1"), "value 1");
        assertEquals(table.size(), 1);
    }

    @Test
    public void sizeTest() {
        table.put("key1", "value 1");
        table.put("key2", "value 2");
        table.remove("key1");
        assertEquals(table.size(), 1);
    }

    @After
    public void tearDown() throws Exception {
        try {
            ShellController.deleteDirectory(databaseDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
