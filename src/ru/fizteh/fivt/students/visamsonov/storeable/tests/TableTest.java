package ru.fizteh.fivt.students.visamsonov.storeable.tests;

import org.junit.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.visamsonov.storage.StructuredTableDirectory;

public class TableTest {

	private Table testedTable;
	private TableProvider provider;
	private static final String TABLE_NAME = "test";
	private static final List<Class<?>> TABLE_TYPES = new ArrayList<Class<?>>(Arrays.asList(Integer.class, Boolean.class, String.class));

	public String serialize (Storeable value) {
		return provider.serialize(testedTable, value);
	}

	public Storeable deserialize (String value) throws ParseException {
		return provider.deserialize(testedTable, value);
	}

	@Before
	public void before () throws Exception {
		new File("/tmp/dbtest").mkdirs();
		provider = new StructuredTableDirectory("/tmp/dbtest");
		try {
			provider.removeTable(TABLE_NAME);
		}
		catch (IllegalStateException e) {}
		testedTable = provider.createTable(TABLE_NAME, TABLE_TYPES);
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
		testedTable.put("testkey", deserialize("<row><col>5</col><col>true</col><null/></row>"));
		Assert.assertEquals(serialize(testedTable.get("testkey")), "<row><col>5</col><col>true</col><null/></row>");
		testedTable.put("testkey", deserialize("<row><col>1</col><col>true</col><col>test</col></row>"));
		Assert.assertEquals(serialize(testedTable.remove("testkey")), "<row><col>1</col><col>true</col><col>test</col></row>");
		Assert.assertNull(testedTable.get("abcd"));
		Assert.assertNull(testedTable.remove("abcd"));
	}

	@Test
	public void testSize () throws Exception {
		int sizeBefore = testedTable.size();
		testedTable.put("a1", deserialize("<row><null/><null/><null/></row>"));
		testedTable.put("a2", deserialize("<row><null/><null/><null/></row>"));
		testedTable.put("a1", deserialize("<row><col>1</col><null/><null/></row>"));
		Assert.assertEquals(testedTable.size(), sizeBefore + 2);
		Assert.assertEquals(serialize(testedTable.remove("a1")), "<row><col>1</col><null/><null/></row>");
		Assert.assertEquals(serialize(testedTable.remove("a2")), "<row><null/><null/><null/></row>");
	}

	@Test
	public void testRollback () throws Exception {
		testedTable.put("abc1", deserialize("<row><null/><null/><null/></row>"));
		testedTable.remove("abc1");
		testedTable.put("abc1", deserialize("<row><null/><col>true</col><null/></row>"));
		Assert.assertEquals(testedTable.rollback(), 1);
		Assert.assertNull(testedTable.get("abc1"));
	}

	@Test
	public void testCommit () throws Exception {
		testedTable.put("abcd1", deserialize("<row><null/><null/><null/></row>"));
		testedTable.put("abcd2", deserialize("<row><null/><null/><null/></row>"));
		Assert.assertEquals(testedTable.commit(), 2);
		Assert.assertEquals(testedTable.rollback(), 0);
		Assert.assertEquals(serialize(testedTable.get("abcd1")), "<row><null/><null/><null/></row>");
		testedTable.remove("abcd1");
		testedTable.remove("abcd2");
		Assert.assertEquals(testedTable.commit(), 2);
		Assert.assertNull(testedTable.get("abcd2"));
	}

	@Test
	public void testGetColumnsCount () throws Exception {
		Assert.assertEquals(testedTable.getColumnsCount(), TABLE_TYPES.size());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetColumnTypeOOB () throws Exception {
		testedTable.getColumnType(-1);
	}

	@Test
	public void testGetColumnType () throws Exception {
		for (int i = 0; i < TABLE_TYPES.size(); i++) {
			Assert.assertEquals(testedTable.getColumnType(i), TABLE_TYPES.get(i));
		}
	}
}