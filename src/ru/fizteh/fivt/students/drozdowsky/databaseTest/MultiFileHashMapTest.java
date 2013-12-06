package ru.fizteh.fivt.students.drozdowsky.databaseTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.drozdowsky.commands.ShellController;
import ru.fizteh.fivt.students.drozdowsky.database.MultiFileHashMap;

import java.awt.geom.IllegalPathStateException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class MultiFileHashMapTest {
    MultiFileHashMap provider;
    File databaseDir;

    @Before
    public void setUp() {
        String workingDir = System.getProperty("user.dir") + "/" + "test";
        while (new File(workingDir).exists()) {
            workingDir = workingDir + "1";
        }
        databaseDir = new File(workingDir);
        databaseDir.mkdir();
        provider = new MultiFileHashMap(workingDir);
    }

    @Test(expected = IllegalPathStateException.class)
    public void badDirectoryPathShouldFail() {
        MultiFileHashMap badProvider = new MultiFileHashMap("abacaba");
    }

    @Test
    public void createAndGetForSameTableShouldBeEqual() {
        assertEquals(provider.createTable("table"), provider.getTable("table"));
    }

    @Test
    public void createGetRemoveTest() {
        assertNotNull(provider.createTable("table"));
        assertNotNull(provider.getTable("table"));
        provider.removeTable("table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullShouldFail() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullShouldFail() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullShouldFail() {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void removeNonExistentShouldFail() {
        provider.removeTable("nothing");
    }

    @After
    public void tearDown() {
        try {
            ShellController.deleteDirectory(databaseDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
