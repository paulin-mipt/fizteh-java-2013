package ru.fizteh.fivt.students.demidov.parallel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.demidov.storeable.StoreableImplementation;
import ru.fizteh.fivt.students.demidov.storeable.StoreableTable;
import ru.fizteh.fivt.students.demidov.storeable.StoreableTableProvider;

public class ParallelTableTest {
	private volatile StoreableTable currentTable;
	private StoreableTableProvider currentProvider;
	private StoreableImplementation value_1, value_2;
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
		
		type = new ArrayList<Class<?>>() {{add(Integer.class);}};
		currentTable = currentProvider.createTable("createdTable", type);
		
		value_1 = new StoreableImplementation(currentTable);
		value_2 = new StoreableImplementation(currentTable);
		value_1.setColumnAt(0, 589);
		value_2.setColumnAt(0, -1204);
	}
	
	@Test 
	public void testDiffsIndependence() {
		currentTable.put("key_1", value_1);
		
		Thread anotherThread = new Thread() {
			public void run() {
				currentTable.put("key_1", value_2);
				currentTable.put("key_2", value_2);
			}
		};
		
		try {
			anotherThread.start();
			anotherThread.join();
		} catch (InterruptedException catchedException) {
		}
		
		Assert.assertEquals("wrong diff", value_1, currentTable.get("key_1"));
		Assert.assertNull("wrong diff", currentTable.get("key_2"));
	}
	
	@Test 
	public void testCommit() {		
		Thread anotherThread = new Thread() {
			public void run() {
				currentTable.put("key", value_1);	
				try {
					currentTable.commit();
				} catch (IOException catchedException) {
					Assert.fail(catchedException.getMessage());
				}
			}
		};
		
		try {
			anotherThread.start();
			anotherThread.join();
		} catch (InterruptedException catchedException) {
		}
		
		Assert.assertEquals("unable to get committed value", value_1, currentTable.get("key"));
		Assert.assertEquals("wrong size", currentTable.size(), 1);
	}

	@After
	public void tearDown() {
		currentProvider.removeTable("createdTable");
	}  
}
