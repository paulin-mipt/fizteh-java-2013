package ru.fizteh.fivt.students.demidov.storeable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StoreableTableTest {
	private StoreableTable currentTable;
	private StoreableTableProvider currentProvider;
	private StoreableImplementation value1, value2;
	private List<Class<?>> type;
	
	@Before
	public void setUp() throws IOException {
		try {
			File tempDirectory = null;
			try {
				tempDirectory = File.createTempFile("StoreableTableProviderTest", null);
			} catch (IOException catchedException) {
				return;
			}
			if (!tempDirectory.delete()) {
				return;
			}
			if (!tempDirectory.mkdir()) {
				return;
			}
			currentProvider = new StoreableTableProvider(tempDirectory.getPath());
		} catch (IllegalArgumentException catchedException) {
			Assert.fail("unable to create StoreableTableProvider example");
		}
		
		type = new ArrayList<Class<?>>() {{add(Integer.class); add(Double.class); add(String.class);}};
		currentTable = currentProvider.createTable("createdTable", type);
		
		value1 = new StoreableImplementation(currentTable);
		value2 = new StoreableImplementation(currentTable);
		value1.setColumnAt(0, 589);
		value1.setColumnAt(1, 3.14);
		value1.setColumnAt(2, "just string");
		value2.setColumnAt(0, -1204);
		value2.setColumnAt(1, 2.71);
		value2.setColumnAt(2, "6 a.m.");
	}

	//test put
	@Test
	public void testPutWithNewKey() {
		currentTable.put("key_1", value1);
		Assert.assertEquals("expected put value", value1, currentTable.get("key_1"));
	}
	
	@Test
	public void testPutWithOverwriting() {
		currentTable.put("key_1", value1);
		currentTable.put("key_1", value2);
		Assert.assertEquals("expected second put value", value2, currentTable.get("key_1"));
	}

	//test remove
	@Test
	public void removeTest() {
		currentTable.put("key_1", value1);
		currentTable.put("key_2", value2);
		currentTable.remove("key_2");

		Assert.assertNull("expected null when get removed value", currentTable.get("key_2"));
	}
	
	//test get
    @Test
	public void testGet() {
    	currentTable.put("key_1", value1);
    	currentTable.put("key_2", value1);
    	currentTable.remove("key_1");
    	Assert.assertNotNull("expected value", currentTable.get("key_2"));
	}
    
	@Test
	public void testGetAfterRemove() {
    	currentTable.put("key_1", value1);
    	currentTable.remove("key_1");
    	Assert.assertNull("expected null when get removed value", currentTable.get("key_1"));
	}
    
    //name and size checks	
	@Test
	public void checkSize() {
		currentTable.put("key_1", value1);
		currentTable.put("key_2", value1);
		currentTable.put("key_3", value1);
		Assert.assertEquals("Incorrect size", 3, currentTable.size());
	}
	
	@Test
	public void checkName() {
		Assert.assertEquals("wrong table name", "createdTable", currentTable.getName());
	}
	
	//commit and rollback tests
	@Test
	public void testCommit() throws IOException {
		currentTable.put("key_1", value1);
		currentTable.put("key_2", value2);
		currentTable.remove("key_1");
		Assert.assertEquals("there is only one diff", 1, currentTable.commit());
		Assert.assertEquals("expected commited put key_2 with value_2", value2, currentTable.get("key_2"));
	}

	@Test
	public void testRollback() throws IOException {
		currentTable.put("key_1", value1);
		currentTable.commit();
		currentTable.remove("key_1");
		Assert.assertEquals("expected rollback 1 key", 1, currentTable.rollback());
		Assert.assertEquals("expected rollback remove key_1", value1, currentTable.get("key_1"));
	}

	
	//null parameters
	@Test(expected = IllegalArgumentException.class)
	public void putWithNullKey() {
		currentTable.put(null, value1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void putWithNullValue() {
		currentTable.put("key_1", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeWithNullKey() {
		currentTable.remove(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getWithNullKey() {
		currentTable.get(null);
	}
	
	//check columns' count
	@Test 
	public void checkColumnsCount() {
		Assert.assertEquals("wrong columns count", 3, currentTable.getColumnsCount());
	}

	//test get column type
	@Test 
	public void columnTypeIsCorrect() {
		Assert.assertEquals("wrong column type", Double.class, currentTable.getColumnType(1));
	}
	
	@Test(expected = IndexOutOfBoundsException.class) 
	public void checkLargeIndex() {
		currentTable.getColumnType(100500);
	}

	@Test(expected = IndexOutOfBoundsException.class) 
	public void checkUnnaturalIndex() {
		currentTable.getColumnType(-1);
	}
	
	@After
	public void tearDown() {
		currentProvider.removeTable("createdTable");
	}  
} 
