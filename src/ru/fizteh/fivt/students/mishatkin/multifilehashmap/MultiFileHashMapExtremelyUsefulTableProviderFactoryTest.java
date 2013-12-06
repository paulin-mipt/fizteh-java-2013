package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Vladimir Mishatkin on 11/4/13
 */
public class MultiFileHashMapExtremelyUsefulTableProviderFactoryTest extends SandboxTest {

	private MultiFileHashMapExtremelyUsefulTableProviderFactory targetFactory = new MultiFileHashMapExtremelyUsefulTableProviderFactory();

	@Before
	public void prepareFactory() throws Exception{
		super.prepare();
	}

	@After
	public void reversePrepareFactory() throws Exception{
		super.reversePrepare();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullArgument() throws Exception {
		targetFactory.create(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnexistedDirectoryArgument() throws Exception {
		targetFactory.create("DirectoryThatCannotBeNamedBecauseItIsSoVeryLongAndDoesnotExistAndAlsoItDoesntHaveAnySpacesInItsNameAndMeanwhileTheQuickBrownFoxJumpsOverTheLazyDog");
	}

	@Test
	public void testCreateNormalDatabase() throws Exception {
		targetFactory.create(sandbox.getName());
	}

}
