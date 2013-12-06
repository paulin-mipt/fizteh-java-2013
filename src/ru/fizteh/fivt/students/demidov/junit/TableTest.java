package ru.fizteh.fivt.students.demidov.junit;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TableTest {
	private TableImplementation currentTable;
	private TableProviderImplementation currentProvider;
	
	@Before
	public void setUp() {
		try {
			File tempDirectory = null;
			try {
				tempDirectory = File.createTempFile("TableProviderImplementationTest", null);
			} catch (IOException catchedException) {
				return;
			}
			if (!tempDirectory.delete()) {
				return;
			}
			if (!tempDirectory.mkdir()) {
				return;
			}
			currentProvider = new TableProviderImplementation(tempDirectory.getPath());
		} catch (IllegalArgumentException catchedException) {
			Assert.fail("unable to create TableProviderImplementation example");
		}
		currentTable = currentProvider.createTable("createdTable");
	}

	//test put
	@Test
	public void testPutWithNewKey() {
		currentTable.put("key_1", "value");
		Assert.assertEquals("expected put value", "value", currentTable.get("key_1"));
	}
	
	@Test
	public void testPutWithOverwriting() {
		currentTable.put("key_1", "value_1");
		currentTable.put("key_1", "newValue");
		Assert.assertEquals("expected second put value", "newValue", currentTable.get("key_1"));
	}

	//test remove
	@Test
	public void removeTest() {
		currentTable.put("key_1", "value_1");
		currentTable.put("key_2", "value_2");
		currentTable.remove("key_2");

		Assert.assertNull("expected null when get removed value", currentTable.get("key_2"));
	}
	
	//test get
    @Test
	public void testGet() {
    	currentTable.put("key_1", "value");
    	currentTable.put("key_2", "value");
    	currentTable.remove("key_1");
    	Assert.assertNotNull("expected value", currentTable.get("key_2"));
	}
    
	@Test
	public void testGetAfterRemove() {
    	currentTable.put("key_1", "value");
    	currentTable.remove("key_1");
    	Assert.assertNull("expected null when get removed value", currentTable.get("key_1"));
	}
    
    //name and size checks	
	@Test
	public void checkSize() {
		currentTable.put("key_1", "value");
		currentTable.put("key_2", "value");
		currentTable.put("key_3", "value");
		Assert.assertEquals("Incorrect size", 3, currentTable.size());
	}
	
	@Test
	public void checkName() {
		Assert.assertEquals("wrong table name", "createdTable", currentTable.getName());
	}
	
	//commit and rollback tests
	@Test
	public void testCommit() {
		currentTable.put("key_1", "value_1");
		currentTable.put("key_2", "value_2");
		currentTable.remove("key_1");
		Assert.assertEquals("there is only one diff", 1, currentTable.commit());
		Assert.assertEquals("expected commited put key_2 with value_2", "value_2", currentTable.get("key_2"));
	}

	@Test
	public void testRollback() {
		currentTable.put("key_1", "value");
		currentTable.commit();
		currentTable.remove("key_1");
		Assert.assertEquals("expected rollback 1 key", 1, currentTable.rollback());
		Assert.assertEquals("expected rollback remove key_1", "value", currentTable.get("key_1"));
	}

	
	//null parameters
	@Test(expected = IllegalArgumentException.class)
	public void putWithNullKey() {
		currentTable.put(null, "value");
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
	
	@After
	public void tearDown() {
		currentProvider.removeTable("createdTable");
	}
}
