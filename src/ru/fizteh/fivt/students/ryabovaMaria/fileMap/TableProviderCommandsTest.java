package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TableProviderCommandsTest {
    private TableProviderFactory tempFactory;
    private TableProvider tempTableProvider;
    private File correctTable;
    private File createdFolder;
    private List<Class<?>> types;
    private File signFile;
            
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();;
    
    @Before
    public void initTempTable() throws IOException {
        tempFactory = new MyTableProviderFactory();
        createdFolder = tempFolder.newFolder("workFolder");
        tempTableProvider = tempFactory.create(createdFolder.toString());
        assertNotNull("Object of TableProvider shouldn't be null", tempTableProvider);
        File table = new File(createdFolder, "table");
        table.mkdir();
        correctTable = new File(createdFolder, "correctTable");
        correctTable.mkdir();
        signFile = new File(correctTable, "signature.tsv");
        signFile.createNewFile();
        types = new ArrayList();
        types.add(int.class);
        types.add(byte.class);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getTableNameIsNull() {
        tempTableProvider.getTable(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getTableNameIsEmpty() {
        tempTableProvider.getTable("");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void  getTableNameIsIncorrect() {
        tempTableProvider.getTable("my\\table.something/wrong");
    }
    
    @Test
    public void getTableNameNotExists() {
        Table tempTable = tempTableProvider.getTable("table13");
        assertNull("Object of Table should be null", tempTable);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getWithoutSignFile() {
        Table tempTable = tempTableProvider.getTable("table");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getEmtySign() {
        Table tempTable = tempTableProvider.getTable("correctTable");
    }
    
    @Test
    public void getCorrect() throws IOException {
        try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("String");
        }
        Table tempTable = tempTableProvider.getTable("correctTable");
    }
    
    @Test
    public void getOneTableObject() throws IOException {
        try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("String");
        }
        Table tempTableOne = tempTableProvider.getTable("correctTable");
        Table tempTableTwo = tempTableProvider.getTable("correctTable");
        assertTrue("Fail: different objects", tempTableOne == tempTableTwo);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createTableNameNull() throws IOException {
        tempTableProvider.createTable(null, types);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createTableWithIncorrectName() throws IOException {
        tempTableProvider.createTable("my.new\\incorrect/table", types);
    }
    
    @Test
    public void createTableWithExistsName() throws IOException {
        Table tempTable = tempTableProvider.createTable("table", types);
        assertNull("Object should be null", tempTable);
    }
    
    @Test
    public void createCorrectTable() throws IOException {
        Table tempTable = tempTableProvider.createTable("table3", types);
        assertNotNull("Object shouldn't be null", tempTable);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void removeTableWithNullName() throws IOException {
        tempTableProvider.removeTable(null);
    }
    
    @Test (expected = IllegalStateException.class)
    public void removeTableWithNotExistsName() throws IOException {
        tempTableProvider.removeTable("table13");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void removeTableWithIncorrectName() throws IOException {
        tempTableProvider.removeTable("deleteIncorrectName\ntable");
    }
    
    @Test
    public void removeCorrectTable() throws IOException {
        tempTableProvider.removeTable("table");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void deserialiseNullTable() throws ParseException {
        String value = "[\"value\"]";
        tempTableProvider.deserialize(null, value);
    }
    
    @Test
    public void deserialiseNullValue() throws ParseException, IOException {
        try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("String");
        }
        Table tempTable = tempTableProvider.getTable("correctTable");
        Storeable temp = tempTableProvider.deserialize(tempTable, null);
        assertNull("Returned storable should be null", temp);
    }
    
    @Test (expected = ParseException.class)
    public void deserialiseWrong() throws IOException, ParseException {
        try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("String");
        }
        Table tempTable = tempTableProvider.getTable("correctTable");
        Storeable temp = tempTableProvider.deserialize(tempTable, "[12]");
    }
    
    @Test (expected = ParseException.class)
    public void deserialiseWrongNumber() throws IOException, ParseException {
        try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("String");
        }
        Table tempTable = tempTableProvider.getTable("correctTable");
        tempTableProvider.deserialize(tempTable, "[\"value\", 12]");
    }
    
    @Test
    public void deserialiseCorrect() throws IOException, ParseException {
        try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("String");
        }
        Table tempTable = tempTableProvider.getTable("correctTable");
        Storeable tempStoreable = tempTableProvider.deserialize(tempTable, "[\"value\"]");
        assertTrue("incorrect value", tempStoreable.getStringAt(0).equals("value"));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void serialiseNullTable() throws IOException {
        List<Object> values = new ArrayList();
        values.add(100000);
        values.add(1);
        Storeable tempStoreable = new StoreableCommands(values, types);
        tempTableProvider.serialize(null, tempStoreable);
    }
    
    @Test
    public void serialiseNullValues() throws IOException {
       try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("String");
       }
       Table tempTable = tempTableProvider.getTable("correctTable");
       String tempString = tempTableProvider.serialize(tempTable, null);
       assertNull("result should be null", tempString);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void serialiseWrong() throws IOException {
       try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("int byte");
       }
       Table tempTable = tempTableProvider.getTable("correctTable");
       List<Object> values = new ArrayList();
       values.add("string");
       values.add(1);
       Storeable tempStoreable = new StoreableCommands(values, types);
       tempTableProvider.serialize(tempTable, tempStoreable);
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void serialiseWrongNumber() throws IOException {
       try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("int byte");
       }
       Table tempTable = tempTableProvider.getTable("correctTable");
       List<Object> values = new ArrayList();
       values.add(10000000);
       values.add(1);
       values.add("strange");
       Storeable tempStoreable = new StoreableCommands(values, types);
       tempTableProvider.serialize(tempTable, tempStoreable);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createForNullTable() {
        tempTableProvider.createFor(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createForNullValues() throws IOException {
        try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("int byte");
        }
        Table tempTable = tempTableProvider.getTable("correctTable");
        tempTableProvider.createFor(tempTable, null);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void createForIncorrectValues() throws IOException {
        try (FileWriter sign = new FileWriter(signFile)) {
            sign.write("int byte");
        }
        Table tempTable = tempTableProvider.getTable("correctTable");
        List<Object> values = new ArrayList();
        values.add(1000000);
        values.add(true);
        tempTableProvider.createFor(tempTable, values);
    }
}
