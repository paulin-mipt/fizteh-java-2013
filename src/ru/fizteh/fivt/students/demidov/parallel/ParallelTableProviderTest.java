package ru.fizteh.fivt.students.demidov.parallel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.demidov.storeable.StoreableTableProvider;

public class ParallelTableProviderTest {
	private volatile StoreableTableProvider currentProvider;
	static Table currentTable;
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
	}

	@Test
	public void getRemoved() throws IOException {
		currentProvider.createTable("createdTable", type);
		
		Thread anotherThread = new Thread() {
			public void run() {
				currentProvider.removeTable("createdTable");
			}
		};
		
		try {
			anotherThread.start();
			anotherThread.join();
		} catch (InterruptedException catchedException) {
		}
	
		Assert.assertNull("got removed table", currentProvider.getTable("createdTable"));
	}
	
	@Test
	public void getCreated() throws IOException {
		Thread anotherThread = new Thread() {
			public void run() {
				try {
					currentTable = currentProvider.createTable("createdTable", type);
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

		Assert.assertSame("expected created table", currentProvider.getTable("createdTable"), currentTable);

		currentProvider.removeTable("createdTable");
	}
}
