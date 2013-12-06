package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Vladimir Mishatkin on 11/4/13
 */
public class MultiFileHashMapReceiverTest extends SandboxTest {
	private MultiFileHashMapReceiver targetTableProvider;

	@Before
	public void prepareFactory() throws Exception{
		super.prepare();
		targetTableProvider = new MultiFileHashMapReceiver(sandbox.getName());
	}

	@After
	public void reversePrepareFactory() throws Exception{
		super.reversePrepare();
	}

	//	create tests
	@Test(expected = IllegalArgumentException.class)
	public void testNullArgument() throws Exception {
		targetTableProvider.createTable(null);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testCreatableName() throws Exception {
		targetTableProvider.createTable("<>.......   weqw()-+");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testUncreatableName() throws Exception {
		targetTableProvider.createTable("alsjd///1<>?!_-~./");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testOtherUncreatableNameCreate() throws Exception {
		targetTableProvider.createTable("///1<>?!_-~./");
	}

	@Test
	public void testReadableNameCreate() throws Exception {
		Assert.assertNotNull(targetTableProvider.createTable("readableTableName"));
	}

	@Test
	public void testExistedTableCreate() throws Exception {
		targetTableProvider.createTable("IWillExist");
		Assert.assertNull(targetTableProvider.createTable("IWillExist"));
	}

	//	get tests
	@Test(expected = IllegalArgumentException.class)
	public void testNullTableGet() throws Exception {
		targetTableProvider.getTable(null);
	}

	@Test
	public void testCreatableNameOfUnexistedTableGet() throws Exception {
		Assert.assertNull(targetTableProvider.getTable("qwidb"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUncreatableNameOfUnexistedTableGet() throws Exception {
		targetTableProvider.getTable("alsjd///1<>?!_-~./");
	}

	@Test
	public void testOKNameTableGet() throws Exception {
		Assert.assertNull(targetTableProvider.getTable("greatTable"));
	}

	@Test
	public void testExistedTableGetNCreate() throws Exception {
		//	Dragon names begin with a capital letter!
		MultiFileHashMapTableReceiver Deathwing = (MultiFileHashMapTableReceiver) targetTableProvider.createTable("Dragon");
		Assert.assertEquals(Deathwing, targetTableProvider.getTable("Dragon"));
		MultiFileHashMapTableReceiver Galakrond = (MultiFileHashMapTableReceiver) targetTableProvider.createTable("Dragon");
		Assert.assertNull(Galakrond);
	}

	//	remove tests
	@Test
	public void testOKTableRemoval() throws Exception {
		targetTableProvider.createTable("dungeon");
		targetTableProvider.removeTable("dungeon");
		targetTableProvider.createTable("dragon");
		targetTableProvider.createTable("muffin");
		targetTableProvider.removeTable("muffin");
		targetTableProvider.removeTable("dragon");
	}

	@Test(expected = IllegalStateException.class)
	public void testUnexistedTableRemove() throws Exception {
		targetTableProvider.removeTable("obliterated");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullArgumentRemove() throws Exception {
		targetTableProvider.removeTable(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadTableRemove() throws Exception {
		targetTableProvider.removeTable("alsjd///1<>?!_-~./");
	}

}
