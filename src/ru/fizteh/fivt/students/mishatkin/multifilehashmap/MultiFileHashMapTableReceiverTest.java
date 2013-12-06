package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.swing.text.html.parser.TagElement;

/**
 * Created by Vladimir Mishatkin on 11/4/13
 */
public class MultiFileHashMapTableReceiverTest extends SandboxTest {
	private MultiFileHashMapTableReceiver targetTable;
	@Before
	public void prepareFactory() throws Exception{
		super.prepare();
		MultiFileHashMapReceiver provider = new MultiFileHashMapReceiver(sandbox.getName());
		targetTable = new MultiFileHashMapTableReceiver("TheMightyTable");
		targetTable.setDelegate(provider);
		provider.createCommand(targetTable.getName());
	}

	@After
	public void reversePrepareFactory() throws Exception{
		super.reversePrepare();
	}

	@Test
	public void testOverwriteValuePut() throws Exception {
		targetTable.put("a", "c");
		Assert.assertEquals(targetTable.put("a", "b"), "c");
	}

	@Test
	public void testGetOverwritten() throws Exception {
		targetTable.put("a", "z");
		targetTable.put("a", "x");
		Assert.assertEquals(targetTable.get("a"), "x");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullKeyPut() throws Exception {
		targetTable.put(null, "siz,jhb ");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullValuePut() throws Exception {
		targetTable.put("1231231245", null);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullGet() throws Exception {
		targetTable.get(null);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullRemove() throws Exception {
		targetTable.remove(null);
	}

	@Test
	public void testCanonicalPut() throws Exception {
		Assert.assertNull(targetTable.put("a", "z"));
	}

	@Test
	public void testCanonicalRemove() throws Exception {
		targetTable.put("quick fox", "lazy dog");
		Assert.assertEquals(targetTable.remove("quick fox"), "lazy dog");
	}

	@Test
	public void testCanonicalGet() throws Exception {
		targetTable.put("key", "value");
		Assert.assertEquals(targetTable.get("key"), "value");
		targetTable.put("руззкийезык", "точноруззкий");
		Assert.assertEquals(targetTable.get("руззкийезык"), "точноруззкий");
		targetTable.put("作弊", "沒有勺子");
		Assert.assertEquals(targetTable.get("作弊"), "沒有勺子");
	}

	@Test
	public void testSize() throws Exception {
		Assert.assertEquals(targetTable.size(), 0);
		targetTable.putCommand("onion", "12i3y4edjv");
		targetTable.putCommand("cucumber", "iysb812");
		targetTable.putCommand("onion", "iysb812");
		Assert.assertEquals(targetTable.size(), 2);
		targetTable.removeCommand("onion");
		Assert.assertEquals(targetTable.size(), 1);
	}

	@Test
	public void testSizeAfterCommitOrRollback() {
		targetTable.put("commit", "pyah");
		targetTable.put("qwiue", "ugr823e");
		targetTable.put("commit", "someOtherPyah");
		targetTable.put("also commit", "also pyah");
		Assert.assertEquals(3, targetTable.commit());
		Assert.assertEquals(3, targetTable.size());
		targetTable.remove("commit");
		Assert.assertEquals(1, targetTable.getUnstagedChangesCount());
		targetTable.remove("cucumber");
		Assert.assertEquals(1, targetTable.getUnstagedChangesCount());
		targetTable.put("lobster", "claw");
		Assert.assertEquals(2, targetTable.getUnstagedChangesCount());
		Assert.assertEquals(3, targetTable.size());
	}

	@Test
	public void testUnexistedKeyRemove() throws Exception {
		Assert.assertNull(targetTable.remove("IDontExist"));
	}

	@Test
	public void testUnexistedKeyGet() throws Exception {
		Assert.assertNull(targetTable.get("NOEXIST!!!!111oneone"));
	}

	@Test
	public void testJustRemovedKeyGet() throws Exception {
		targetTable.put("exist", "a");
		targetTable.put("wont exist", "b");
		Assert.assertEquals(targetTable.get("wont exist"), "b");
		targetTable.put("exist", "b");
		targetTable.remove("wont exist");
		Assert.assertNull(targetTable.get("wont exist"));
	}

	@Test
	public void testCanonicalCommit() throws Exception {
		Assert.assertEquals(0, targetTable.commit());
	}

	@Test
	public void testCanonicalRollback() throws Exception {
		Assert.assertEquals(0, targetTable.rollback());
	}

	@Test
	public void testRollbackThePut() throws Exception {
		targetTable.put("faceless", "void");
		targetTable.rollback();
		Assert.assertNull(targetTable.get("faceless"));
	}

	@Test
	public void testCommitThePut() throws Exception {
		targetTable.put("a", "b");
		Assert.assertEquals("b", targetTable.get("a"));
		Assert.assertEquals(1, targetTable.commit());
		Assert.assertEquals("b", targetTable.get("a"));
	}

	@Test
	public void testRollbackAfterCommit() throws Exception {
		targetTable.put("stuff", "not junk");
		targetTable.commit();
		targetTable.remove("stuff");
		targetTable.rollback();
		Assert.assertEquals("not junk", targetTable.get("stuff"));
	}

}
