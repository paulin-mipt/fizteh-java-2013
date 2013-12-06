package ru.fizteh.fivt.students.demidov.storeable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class StoreableTableProviderTest {
	private StoreableTableProvider currentProvider;
	private List<Class<?>> type, incorrectType;
	private List<Object> value, incorrectValue, shortValue, longValue;
	private String serializedValue = "<row><col>12</col><col>3.14</col><col>just string</col></row>";
	private String incorrectSerializedValue = "<row><trouble>12</trouble><a>3.14</a><c>just string</c></row>";
	
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
		incorrectType = new ArrayList<Class<?>>() {{add(Double.class); add(Float.class); add(Boolean.class);}};

		value = new ArrayList<Object>() {{add(Integer.valueOf("12")); add(Double.valueOf("3.14")); add(String.valueOf("just string"));}};
		incorrectValue = new ArrayList<Object>() {{add(Float.valueOf("2.7f")); add(Boolean.valueOf("true")); add(new String("put it back"));}};
		shortValue = new ArrayList<Object>() {{add(Integer.valueOf("12"));}};
		longValue = new ArrayList<Object>() {{add(Integer.valueOf("12")); add(Double.valueOf("3.14")); add(String.valueOf("just string")); add(Boolean.valueOf("true"));}};
	}	

	@Test 
	public void serializeCorrectValue() throws IOException {
		Table table = currentProvider.createTable("createdTable", type);
		Storeable gotStoreable = currentProvider.createFor(table, value);
		Assert.assertEquals("incorrect serialization", serializedValue, currentProvider.serialize(table, gotStoreable));
	}

	@Test(expected = ColumnFormatException.class)
	public void serializeIncorrectValue() throws IOException {
		Table table = currentProvider.createTable("firstTable", type);
		StoreableImplementation alienStoreable = new StoreableImplementation(currentProvider.createTable("secondTable", incorrectType));
		alienStoreable.setColumnAt(0, 2.71);
		System.out.println(currentProvider.serialize(table, alienStoreable));
	}

	@Test 
	public void deserializeCorrectValue() throws IOException, ParseException {
		Table table = currentProvider.createTable("createdTable", type);
		Storeable gotValue = currentProvider.deserialize(table, serializedValue);
		
		Assert.assertEquals("incorrect deserialization", gotValue.getIntAt(0), (Integer)12);
		Assert.assertEquals("incorrect deserialization", gotValue.getDoubleAt(1), (Double)3.14);
		Assert.assertEquals("incorrect deserialization", gotValue.getStringAt(2), "just string");
	}

	@Test(expected = ParseException.class)
	public void deserializeIncorrectValue() throws IOException, ParseException {
		Table table = currentProvider.createTable("createdTable", type);
		currentProvider.deserialize(table, incorrectSerializedValue);
	}
	
	@Test
	public void createForValue() throws IOException {
		Table table = currentProvider.createTable("createdTable", type);
		table.put("key", currentProvider.createFor(table, value));
	}

	@Test(expected = ColumnFormatException.class)
	public void createForIncorrectValue() throws IOException {
		Table table = currentProvider.createTable("createdTable", type);
		currentProvider.createFor(table, incorrectValue);
	}

	@Test(expected = IndexOutOfBoundsException.class) 
	public void checkShortValue() throws IOException {
		Table table = currentProvider.createTable("createdTable", type);
		currentProvider.createFor(table, shortValue);
	}

	@Test(expected = IndexOutOfBoundsException.class) 
	public void checkLongValue() throws IOException {
		Table table = currentProvider.createTable("createdTable", type);
		currentProvider.createFor(table, longValue);
	}
	
	@Test
	public void getTableAfterCreate() throws IOException {
		Table createdTable = currentProvider.createTable("createdTable", type);
		Assert.assertEquals("should be createdTable", "createdTable", currentProvider.getTable("createdTable").getName());
		Table table = currentProvider.getTable("createdTable");
		Assert.assertSame("expected the same table as created", createdTable, table);
		table = currentProvider.getTable("createdTable");
		Assert.assertSame("expected the same table as created", createdTable, table);
		currentProvider.removeTable("createdTable");
	}	
	
	@Test
	public void getTableAfterRemove() throws IOException {
		currentProvider.createTable("createdTable", type);
		currentProvider.removeTable("createdTable");
		Assert.assertNull("expected null", currentProvider.getTable("createdTable"));
	}	
	
	//undefined parameters
	@Test(expected = IllegalStateException.class)
	public void removeTableWithNullParamedter() {
		currentProvider.removeTable("ufo");
	}	

	@Test
	public void createWithNullParameter() {
		Assert.assertNull("expected null", currentProvider.getTable("ufo"));
	}	
	
	//null parameters
	@Test(expected = IllegalArgumentException.class)
	public void createTableWithNullParameter() throws IOException {
		currentProvider.createTable(null, type);
	}	
	
	
	@Test(expected = IllegalArgumentException.class)
	public void createTableWithNullTypes() throws IOException {
		currentProvider.createTable("fakeTable", null);
	}	

	@Test(expected = IllegalArgumentException.class)
	public void getTableWithNullParameter() {
		currentProvider.getTable(null);
	}	

	@Test(expected = IllegalArgumentException.class)
	public void removeTableWithNullParameter() {
		currentProvider.removeTable(null);
	}
}
