package ru.fizteh.fivt.students.demidov.storeable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

public class StoreableTest {
	private StoreableImplementation value;
	@Before
	public void setUp() throws IOException {
		StoreableTableProvider currentProvider = null;
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
		
		List<Class<?>> type = new ArrayList<Class<?>>() {{add(Integer.class); add(String.class); add(Long.class); add(Byte.class); 
									add(Boolean.class); add(Float.class); add(Double.class);}};

		value = new StoreableImplementation(currentProvider.createTable("createdTable", type));

		value.setColumnAt(0, -1204);
		value.setColumnAt(1, "just string");
		value.setColumnAt(2, 865123456711L);
		value.setColumnAt(3, Byte.valueOf((byte)5));
		value.setColumnAt(4, true);
		value.setColumnAt(5, 2.71f);
		value.setColumnAt(6, 3.14);
	}

	//test get from correct column
	@Test
	public void getIntFromColumn() {
		Assert.assertEquals(value.getIntAt(0), (Integer)(-1204));
	}
	
	@Test
	public void getStringFromColumn() {
		Assert.assertEquals(value.getStringAt(1), "just string");
	}

	@Test
	public void getLongFromColumn() {
		Assert.assertEquals(value.getLongAt(2), (Long)865123456711L);
	}

	@Test
	public void getByteFromColumn() {
		Assert.assertEquals(value.getByteAt(3), Byte.valueOf((byte)5));
	}
	
	@Test
	public void getBooleanFromColumn() {
		Assert.assertEquals(value.getBooleanAt(4), true);
	}
	
	@Test
	public void getFloatFromColumn() {
		Assert.assertEquals(value.getFloatAt(5), (Float)2.71f);
	}

	@Test
	public void getDoubleFromColumn() {
		Assert.assertEquals(value.getDoubleAt(6), (Double)3.14);
	}
	
	//test get from wrong column	
	@Test(expected = ColumnFormatException.class)
	public void getIntFromWrongColumn() {
		value.getIntAt(1);
	}
	
	@Test(expected = ColumnFormatException.class)
	public void getDoubleFromWrongColumn() {
		value.getDoubleAt(0);
	}

	@Test(expected = ColumnFormatException.class)
	public void getFloatFromWrongColumn() {
		value.getFloatAt(0);
	}
	
	@Test(expected = ColumnFormatException.class)
	public void getStringFromWrongColumn() {
		value.getStringAt(0);
	}

	@Test(expected = ColumnFormatException.class)
	public void getLongFromWrongColumn() {
		value.getLongAt(0);
	}

	@Test(expected = ColumnFormatException.class)
	public void getByteFromWrongColumn() {
		value.getByteAt(0);
	}

	@Test(expected = ColumnFormatException.class)
	public void getBooleanFromWrongColumn() {
		value.getBooleanAt(0);
	}

	//test set
	@Test(expected = ColumnFormatException.class)
	public void setValueToWrongColumn() {
		value.setColumnAt(1, true);
	}
	
	@Test
	public void setNull() {
		value.setColumnAt(0, null);
		Assert.assertNull("unable to set null value", value.getColumnAt(0));
	}

	//test wrong number column use
	@Test(expected = IndexOutOfBoundsException.class)
	public void checkUnnaturalColumn() {
		value.getColumnAt(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void checkNonExistingColumn() {
		value.getColumnAt(100500);
	}
}
