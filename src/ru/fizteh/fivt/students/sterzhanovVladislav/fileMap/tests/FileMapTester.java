package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.FileMap;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.storeable.StoreableRow;

public class FileMapTester {
    Table table;
    List<Class<?>> sampleSignature;
    Storeable sampleValue;
    Storeable otherValue;

    @Before
    public void init() {
        sampleSignature = new ArrayList<Class<?>>();
        sampleSignature.add(String.class);
        List<String> values = new ArrayList<String>();
        values.add("Some string");
        sampleValue = new StoreableRow(sampleSignature, values);
        values = new ArrayList<String>();
        values.add("Other string");
        otherValue = new StoreableRow(sampleSignature, values);
        table = new FileMap("Tested Table", sampleSignature);
    }
    
    @Test
    public void getNameTest() {
        assertEquals(table.getName(), "Tested Table");
    }
    
    @Test
    public void noOpCommitTest() throws IOException {
        table.put("key", sampleValue);
        table.remove("key");
        assertEquals(table.commit(), 0);
    }
    
    @Test
    public void rollbackTest() throws IOException {
        table.put("key", sampleValue);
        table.commit();
        table.put("key", otherValue);
        table.put("new_key", sampleValue);
        table.remove("non-existent_key");
        assertEquals(table.rollback(), 2);
        assertEquals(table.get("key"), sampleValue);
        assertEquals(table.size(), 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void illegalKeyShouldFail() {
        table.put("bad key", sampleValue);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nullKeyShouldFail() {
        table.put(null, sampleValue);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nullValueShouldFail() {
        table.put("key", null);
    }

    @Test
    public void overWriteTest() {
        table.put("key", sampleValue);
        assertEquals(table.put("key", otherValue), sampleValue);
    }
    
    @Test
    public void putGetRemoveSizeCommitTest() throws IOException {
        table.put("key", sampleValue);
        assertEquals(table.get("key"), sampleValue);
        assertEquals(table.size(), 1);
        assertEquals(table.commit(), 1);
        assertEquals(table.remove("key"), sampleValue);
        assertNull(table.remove("key"));
        assertEquals(table.size(), 0);
    }
}
