package ru.fizteh.fivt.students.visamsonov.junit.tests;

import org.junit.*;
import java.io.File;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.visamsonov.storage.MultiFileStorage;

public class TableTest {

	private static Table testedTable;
	private static final String TABLE_NAME = "test";

	@Before
	public void before () throws Exception {
		testedTable = new MultiFileStorage("/tmp/dbtest", TABLE_NAME);
		new File("/tmp/dbtest/" + TABLE_NAME).mkdirs();
	}

	@Test
	public void testGetName () throws Exception {
		Assert.assertEquals(testedTable.getName(), TABLE_NAME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNull () throws Exception {
		testedTable.get(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNl () throws Exception {
		testedTable.get("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveNull () throws Exception {
		testedTable.remove(null);
	}

	@Test
	public void testComplex () throws Exception {
		testedTable.put("testkey", "testvalue");
		Assert.assertEquals(testedTable.get("testkey"), "testvalue");
		testedTable.put("testkey", "testvalue2");
		Assert.assertEquals(testedTable.remove("testkey"), "testvalue2");
		Assert.assertNull(testedTable.get("abcd"));
		Assert.assertNull(testedTable.remove("abcd"));
	}

	@Test
	public void testSize() throws Exception {
		int sizeBefore = testedTable.size();
		testedTable.put("abcdef1", "1");
		testedTable.put("abcdef2", "2");
		testedTable.put("abcdef1", "3");
		Assert.assertEquals(testedTable.size(), sizeBefore + 2);
	}

	@Test
	public void testRollback() throws Exception {
		testedTable.put("abc1", "1");
		testedTable.remove("abc1");
		testedTable.put("abc1", "2");
		Assert.assertEquals(testedTable.rollback(), 1);
		Assert.assertNull(testedTable.get("abc1"));
	}

	@Test
	public void testCommit() throws Exception {
		testedTable.put("abcd1", "1");
		testedTable.put("abcd2", "2");
		Assert.assertEquals(testedTable.commit(), 2);
		Assert.assertEquals(testedTable.get("abcd1"), "1");
		testedTable.remove("abcd1");
		testedTable.remove("abcd2");
		Assert.assertEquals(testedTable.commit(), 2);
		Assert.assertNull(testedTable.get("abcd2"));
	}
}