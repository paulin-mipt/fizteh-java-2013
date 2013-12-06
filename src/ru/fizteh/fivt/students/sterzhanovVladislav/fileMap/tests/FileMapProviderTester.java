package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.FileMapProvider;
import static org.junit.Assert.*;

public class FileMapProviderTester {
    FileMapProvider provider;
    List<Class<?>> sampleSignature;
    
    @Before
    public void init() throws IllegalArgumentException, IOException {
        provider = new FileMapProvider(System.getProperty("user.dir"));
        sampleSignature = new ArrayList<Class<?>>();
        sampleSignature.add(String.class);
    }
    
    @Test
    public void createAndGetForSameTableShouldBeEqual() throws IOException {
        assertEquals(provider.createTable("table", sampleSignature), provider.getTable("table"));
        provider.removeTable("table");
    }
    
    @Test(expected = IllegalStateException.class)
    public void removeNonExistentShouldFail() {
        provider.removeTable("empty");
    }
    
    @Test
    public void addSameNameShouldBeNull() throws IOException {
        provider.createTable("table", sampleSignature);
        assertNull(provider.createTable("table", sampleSignature));
        provider.removeTable("table");
    }
    
    @Test
    public void createGetRemoveTest() throws IOException {
        assertNotNull(provider.createTable("table", sampleSignature));
        assertNotNull(provider.getTable("table"));
        provider.removeTable("table");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullShouldFail() throws IOException {
        provider.createTable(null, sampleSignature);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullSignatureShouldFail() throws IOException {
        provider.createTable("table", null);
        provider.removeTable("table");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getNullShouldFail() {
        provider.getTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeNullShouldFail() {
        provider.removeTable(null);
    }
    
    @Test
    public void createForTest() throws IOException {
        Table testTable = provider.createTable("table", sampleSignature);
        List<String> values = new ArrayList<String>();
        values.add("test value");
        assertNull(testTable.put("key", provider.createFor(testTable, values)));
        assertEquals(testTable.get("key").getStringAt(0), "test value");
        provider.removeTable("table");
    }
    
    @After
    public void wipe() {
    }
}
