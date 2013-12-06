package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class TableCommandsTest {
    TableProviderFactory tempFactory;
    TableProvider tempTableProvider;
    Table tempTable;
    File createdFolder;
    HashMap<String, String> tempMap = new HashMap<String, String>();
    String firstKey;
    String firstValue;
    List<Class<?>> types;
    int length;
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void initTempTable() throws IOException {
        tempFactory = new MyTableProviderFactory();
        createdFolder = tempFolder.newFolder("workFolder");
        tempTableProvider = tempFactory.create(createdFolder.toString());
        assertNotNull("Object of TableProvider shouldn't be null", tempTableProvider);
        types = new ArrayList();
        types.add(String.class);
        types.add(boolean.class);
        tempTable = tempTableProvider.createTable("table", types);
        assertNotNull("Object of Table shouldn't be null", tempTable);
    }
    
    @Test
    public void getNameTest() {
        assertTrue("Table name shold be \"table\"", tempTable.getName().equals("table"));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getNull() {
        tempTable.get(null);
    }
    
    @Test
    public void getNotExistsKey() {
        Storeable value = tempTable.get("notExists");
        assertNull("Value should be null", value);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putKeyEmpty() {
        List<Object> values = new ArrayList();
        values.add("value");
        values.add(true);
        Storeable tempStoreable = new StoreableCommands(values, types);
        tempTable.put("", tempStoreable);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putIllegalKey() {
        List<Object> values = new ArrayList();
        values.add("value");
        values.add(true);
        Storeable tempStoreable = new StoreableCommands(values, types);
        tempTable.put("\n  ", tempStoreable);
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void putLessInValue() {
        List<Object> values = new ArrayList();
        values.add(true);
        Storeable tempStoreable = new StoreableCommands(values, types);
        tempTable.put("key", tempStoreable);
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void putMoreInValue() {
        List<Object> values = new ArrayList();
        values.add("value");
        values.add(true);
        values.add(55);
        Storeable tempStoreable = new StoreableCommands(values, types);
        tempTable.put("key", tempStoreable);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putKeyNull() {
        List<Object> values = new ArrayList();
        values.add("value");
        values.add(true);
        Storeable tempStoreable = new StoreableCommands(values, types);
        tempTable.put(null, tempStoreable);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putValueNull() {
        tempTable.put("key", null);
    }
    
    @Test
    public void putCorretArgs() {
        List<Object> values = new ArrayList();
        values.add("value");
        values.add(true);
        Storeable tempStoreable = new StoreableCommands(values, types);
        Storeable result = tempTable.put("key", tempStoreable);
        assertNull("Result should be null", result);
    }
    
    @Test
    public void putKeyExists() {
        List<Object> valuesOne = new ArrayList();
        valuesOne.add("night");
        valuesOne.add(true);
        Storeable tempStoreableOne = new StoreableCommands(valuesOne, types);
        tempTable.put("good", tempStoreableOne);
        List<Object> valuesTwo = new ArrayList();
        valuesTwo.add("day");
        valuesTwo.add(true);
        Storeable tempStoreableTwo = new StoreableCommands(valuesTwo, types);
        Storeable last = tempTable.put("good", tempStoreableTwo);
        String lastString = last.getStringAt(0);
        assertTrue("Result should be \"night\"", lastString.equals("night"));
    }
    
    public void removeKeyNotExists() {
        assertNull("Result should be null", tempTable.remove("notExists"));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void removeKeyNull() {
        tempTable.remove(null);
    }
    
    @Test
    public void removeCorrectArgs() {
        List<Object> values = new ArrayList();
        values.add("value");
        values.add(true);
        Storeable tempStoreable = new StoreableCommands(values, types);
        tempTable.put("key", tempStoreable);
        tempTable.remove("key");
        assertNull("Value should be null", tempTable.get("key"));
    }
    
    private String generateString() {
        Random rand = new Random();
        StringBuilder tempString = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            char c = (char) (rand.nextInt((int) (Character.MAX_VALUE)));
            tempString.append(c);
        }
        return tempString.toString();
    }
    
    @Test
    public void sizeTest() {
        Random rand = new Random();
        length = rand.nextInt(100);
        for (int i = 0; i < length; ++i) {
            String key = generateString();
            List<Object> values = new ArrayList();
            values.add("value");
            values.add(true);
            Storeable tempStoreable = new StoreableCommands(values, types);
            tempTable.put(key, tempStoreable);
        }
        assertTrue("Incorrect size", tempTable.size() == length);
    }
    
    @Test
    public void generateTable() {
        Random rand = new Random();
        length = rand.nextInt(20);
        firstKey = generateString();
        List<Object> values = new ArrayList();
        values.add("value");
        values.add(true);
        Storeable tempStoreable = new StoreableCommands(values, types);
        tempMap.put(firstKey, firstValue);
        tempTable.put(firstKey, tempStoreable);
        for (int i = 0; i < length; ++i) {
            String key = generateString();
            String value = generateString();
            tempTable.put(key, tempStoreable);
            tempMap.put(key, "[\"value\",true]");
        }
    }
    
    @Test
    public void commitTestPutAndRemoveOneKey() throws IOException {
        generateTable();
        tempMap.remove(firstKey);
        tempTable.remove(firstKey);
        assertTrue("Commit error", tempMap.size() == tempTable.commit());
    }
    
    @Test
    public void commitTestPutOneKeyTwice() throws IOException {
        generateTable();
        List<Object> values = new ArrayList();
        values.add(" ");
        values.add(true);
        Storeable tempStoreable = new StoreableCommands(values, types);
        tempMap.put(firstKey, "[\" \",true]");
        tempTable.put(firstKey, tempStoreable);
        assertTrue("Commit error", tempMap.size() == tempTable.commit());
    }
    
    @Test
    public void rollbackTestRandom() throws IOException {
        commitTestPutAndRemoveOneKey();
        generateTable();
        assertTrue("Rollback error", tempTable.rollback() == (length + 1));
    }
    
    @Test
    public void rollbackTestNoChanges() throws IOException {
        commitTestPutAndRemoveOneKey();
        assertTrue("Rollback error", tempTable.rollback() == 0);
    }
}
