package ru.fizteh.fivt.students.dobrinevski.jUnit.tests;

import org.junit.*;
import static org.junit.Assert.*;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dobrinevski.jUnit.MyTableProvider;

public class MyTableProviderTester {
    public static MyTableProvider provider;

    @Before
    public void init() {
        provider = new MyTableProvider(System.getProperty("user.dir"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDirectoryGiven() {
        TableProvider provider = new MyTableProvider(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDirectoryGiven() {
        TableProvider provider = new MyTableProvider("IReallyHopeThatWeDontHaveDirectoryWithThisName");
    }

    @Test
    public void basicTest() {
        assertNotNull(provider.createTable("table"));
        assertNotNull(provider.getTable("table"));
        assertEquals(provider.createTable("table2"), provider.getTable("table2"));
        provider.removeTable("table");
        provider.removeTable("table2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDirectoryInCreate() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDirectoryInGet() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDirectoryInRemove() {
        provider.removeTable(null);
    }


    @Test(expected = IllegalStateException.class)
    public void removeNonExistentShouldFail() {
        provider.removeTable("IReallyHopeThatWeDontHaveDirectoryWithThisName");
    }
}
