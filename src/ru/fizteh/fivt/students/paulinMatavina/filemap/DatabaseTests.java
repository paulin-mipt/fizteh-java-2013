package ru.fizteh.fivt.students.paulinMatavina.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.*;

public class DatabaseTests {
    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();  
    Table table;
    Table shortTable;
    TableProvider provider;
    MyTableProviderFactory factory;
    File root;
    ArrayList<Class<?>> list;
    ArrayList<Class<?>> wrongList;
    ArrayList<Class<?>> nullList;
    ArrayList<Object> correct;
    ArrayList<Object> wrong;
    ArrayList<Object> shortVal;
    Storeable correctValues;
    Storeable wrongValues;
    Storeable alienValues;
    File file;
    
    @Before
    @Test
    public void initializationOk() throws IOException {
        rootFolder.create();
        root = rootFolder.newFolder("root");
        file = rootFolder.newFile("file");
        factory = new MyTableProviderFactory();
        provider = factory.create(root.getAbsolutePath());
        list = new ArrayList<Class<?>>();
        list.add(String.class);
        list.add(Integer.class);
        list.add(Byte.class);
        list.add(Boolean.class);
        list.add(Float.class);
        
        ArrayList<Class<?>> shortList = new ArrayList<Class<?>>();
        shortList.add(String.class);
        shortTable = provider.createTable("short", shortList);
        shortVal = new ArrayList<Object>();
        shortVal.add("ololo!");
        alienValues = provider.createFor(shortTable, shortVal);
        
        table = provider.createTable("default", list);
        assertNotNull(table);
        wrongValues = provider.createFor(table);
        correct = new ArrayList<Object>();
        correct.add("String");
        correct.add(1000000000);
        correct.add(null);
        correct.add(true);
        correct.add(1.001);
        
        wrong = new ArrayList<Object>(correct);
        wrong.set(1, true);
        
        wrongList = new ArrayList<Class<?>>(list);
        wrongList.set(1, ArrayList.class);
        
        nullList = new ArrayList<Class<?>>(list);
        nullList.set(1, null);
        
        correctValues = provider.createFor(table, correct);
    }  
    
    //tests for TableProviderFactory 
    @Test
    public void testFactoryCreateNonExist() throws IOException {
        provider = factory.create("non-existent-path");
    }  
    
    @Test(expected = IllegalArgumentException.class)
    public void testFactoryCreateOnFile() throws IOException {
        provider = factory.create(file.getAbsolutePath());
    }  
    
    @Test(expected = IllegalArgumentException.class)
    public void testFactoryCreateNull() {
        try {
            provider = factory.create(null);
        } catch (IOException e) {
            fail();
        }
    }   
    //end of tests for TableProviderFactory
    
    //tests for TableProvider
    //provider.getTable()
    @Test(expected = IllegalArgumentException.class)
    public void testProviderGetTableNull() {
        provider.getTable(null);
    } 
    
    @Test
    public void testProviderGetTableNotExisting() {
        table = provider.getTable("not-existing-table");
        assertNull(table);
    }  
    
    @Test(expected = IllegalStateException.class)
    public void testProviderOperationsWithRemovedTable() throws IOException {
        provider.removeTable("default");
        assertNull(provider.getTable("default"));
        table.put("a", correctValues);
    }  
    
    @Test(expected = RuntimeException.class)
    public void testProviderGetTableIncorrect() {
        table = provider.getTable("..");
    } 
    
    //Provider.createTable()
    @Test(expected = IllegalStateException.class)
    public void testProviderNewNameWrong() {
        try {
            table = provider.createTable("nam//e", list);
        } catch (IOException e) {
            fail();
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderNewNameEmpty() {
        try {
            provider.createTable("      ", list);
        } catch (IOException e) {
            fail();
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderCreateTableNull() {
        try {
            provider.createTable(null, list);
        } catch (IOException e) {
            fail();
        }
    } 
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderCreateWrongType() {
        try {
            provider.createTable("def", wrongList);
        } catch (IOException e) {
            fail();
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderCreateNullType() {
        try {
            provider.createTable("def", nullList);
        } catch (IOException e) {
            fail();
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderCreateTableListNull() {
        try {
            provider.createTable("def", null);
        } catch (IOException e) {
            fail();
        }
    } 
    
    @Test
    public void testProviderCreateTableExisting() {
        try {
            table = provider.createTable("default", list);
        } catch (IOException e) {
            fail();
        }
        assertNull(table);
    } 
    
    @Test
    public void testProviderCreateRemoveTableOk() {
        try {
            provider.createTable("myLittlePony", list);
        } catch (IOException e) {
            fail();
        }
        assertNotNull(table);
        try {
            provider.removeTable("myLittlePony");
        } catch (IOException e) {
            fail();
        }
    } 
    
    //provider.removeTable()
    @Test(expected = IllegalStateException.class)
    public void testProviderRemoveTableNotExisting() {
        try {
            provider.removeTable("myLittlePony");
        } catch (IOException e) {
            fail();
        }
    } 
    
    @Test(expected = IllegalStateException.class)
    public void testProviderCreateRemovePut() {
        try {
            table = provider.createTable("myLittleTable", list);
        } catch (IOException e) {
            fail();
        }
        assertNotNull(table);
        try {
            provider.removeTable("myLittleTable");
        } catch (IOException e) {
            fail();
        }
        table.put("put", provider.createFor(table, correct));
    } 
    
    //provider.serialize()
    @Test(expected = ColumnFormatException.class)
    public void testProviderSerializeWrong() {
        provider.serialize(table, alienValues);
    }
    
    @Test
    public void testProviderSerializeOk() {
        provider.serialize(table, correctValues);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderSerializeNull() {
        provider.serialize(table, null);
    }
    
    //provider.deserialize()
    @Test(expected = ParseException.class)
    public void testProviderDeserializeWrong() throws ColumnFormatException, ParseException {
        provider.deserialize(table, provider.serialize(shortTable, alienValues));
    }
    
    @Test
    public void testProviderSerializeDeserializeOk() throws ParseException {
        provider.deserialize(table, provider.serialize(table, correctValues));
    }  
    
    //provider.createFor()
    @Test(expected = ColumnFormatException.class)
    public void testProviderCreateForWrong() throws ParseException {
        provider.createFor(table, wrongList);
    }  
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testProviderCreateForShort() throws ParseException {
        provider.createFor(table, shortVal);
    }   
    //end of tests for TableProvider
    
    //tests for Table  
    //table.put() & table.get() & table.remove()
    @Test(expected = IllegalArgumentException.class)
    public void testTablePutNullKey() {
        table.put(null, correctValues);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTablePutNullValue() {
        table.put("abcd", null);
    }
    
    @Test
    public void testTablePutNotNullValue() {
        table.put("abcd", correctValues);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableGetNullValue() {
        table.put("abcd", null);
    } 
    
    @Test
    public void testTableGetRemoveRollback() {
        table.put("a", correctValues);
        table.remove("a");
        assertEquals(0, table.rollback());
        assertEquals(0, table.size());
    }
    
    @Test
    public void testTablePutRollbackGet() {
        table.put("newK", correctValues);
        table.rollback();
        assertNull(table.get("newK"));
    }
    
    @Test
    public void testTablePutCommitGet() {
        table.put("newK", correctValues);
        try {
            table.commit();
        } catch (IOException e) {
            fail();
        }
        assertEquals(table.get("newK"), correctValues);
        table.remove("newK");
        int result = 0;
        try {
            result = table.commit();
        } catch (IOException e) {
            fail();
        }
        assertEquals(1, result);
    }
    
    @Test
    public void testTableNewRollback() {
        try {
            table = provider.createTable("newT", list);
        } catch (IOException e) {
            fail();
        }
        assertNotNull(table);
        assertEquals(0, table.rollback());
        try {
            provider.removeTable("newT");
        } catch (IOException e) {
            fail();
        }
    }
    
    @Test
    public void testTableNewGet() throws IOException {
        table = provider.createTable("newT", list);
        assertNull(table.get("a"));
        provider.removeTable("newT");
    }
    
    //table.getColumnsCount()
    @Test
    public void testTableGetColumnsCount() {
        assertEquals(5, table.getColumnsCount());
    }
    
    //table.getColumnsCount()
    @Test
    public void testTableGetColumnType() {
        assertEquals(String.class, table.getColumnType(0));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testTableGetWrongColumnType() {
        table.getColumnType(6);
    }
    //end of Table tests 
    
    //Storeable tests
    //storeable.setColumnAt()
    @Test(expected = ColumnFormatException.class)
    public void testStoreableSetColumnAtWrong() {
        correctValues.setColumnAt(0, 1);
    }
    
    @Test(expected = ColumnFormatException.class)
    public void testStoreableSetColumnAtIllegal() {
        correctValues.setColumnAt(2, 1000); //not a byte value
    }
    
    @Test
    public void testStoreableSetColumnAtNull() {
        correctValues.setColumnAt(0, null);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testStoreableSetColumnAtWrongIndex() {
        correctValues.setColumnAt(-1, 1);
    }
    
    //storeable.getColumnAt()
    @Test(expected = IndexOutOfBoundsException.class)
    public void testStoreableGetColumnAtWrongIndex() {
        correctValues.getColumnAt(6);
    }
    
    @Test(expected = ColumnFormatException.class)
    public void testStoreableGetColumnAtWrongType() {
        correctValues.getIntAt(0);
    }
    
    @Test(expected = ColumnFormatException.class)
    public void testStoreableGetColumnAtWrong() {
        correctValues.getIntAt(2); //byte is not int
    }
    
    @Test
    public void testStoreableGetColumnAtStringOk() {
        assertEquals("String", correctValues.getStringAt(0));
    } 
    
    @Test
    public void testStoreableGetColumnAtBoolOk() {
        assertEquals(true, correctValues.getBooleanAt(3));
    }  
    
    @Test
    public void testStoreableGetColumnAtByteOk() {
        assertSame(null, correctValues.getByteAt(2));
    }  
    
    @Test
    public void testStoreableGetColumnAtIntOk() {
        assertEquals((int) 1000000000, (int) correctValues.getIntAt(1));
    }  
     
    //end of Storeable tests 
    
    //close() tests
    @Test(expected = IllegalStateException.class)
    public void testTableCloseOpen() throws Exception {
        table.put("new", correctValues);
        table.commit();
        MyTable oldTable = (MyTable) table;
        oldTable.close();
        table = provider.getTable("default");
        assertNotNull(table.get("new"));
        assertNotEquals(oldTable, table);
        oldTable.get("a");
    } 
    
    @Test(expected = IllegalStateException.class)
    public void testProviderCloseGet() throws Exception {
        ((MyTableProvider) provider).close();
        provider.getTable("default");
    } 
    
    @Test
    public void testTableDoubleClose() throws Exception {
        table.put("new", correctValues);
        table.commit();
        ((MyTable) table).close();
        ((MyTable) table).close();
        table = provider.getTable("default");
        assertNotNull(table.get("new"));
    } 
    
    @Test
    public void testProviderDoubleClose() throws Exception {
        ((MyTableProvider) provider).close();
        ((MyTableProvider) provider).close();
    } 
    
    @Test(expected = IllegalStateException.class)
    public void testFactoryClose() throws Exception {
        ((MyTableProviderFactory) factory).close();
        factory.create(root.getAbsolutePath());
    } 
    //end of close() tests
}
