package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileTableProviderFactory;

/**
 * User: Alexander
 * Date: 28.10.13
 * Time: 0:11
 */
public class MultiFileTableProviderFactoryUnitTest {
    MultiFileTableProviderFactory testManagerCreator;

    @Before
    public void setUpTestObject() {
        testManagerCreator = new MultiFileTableProviderFactory();
    }

    /**
     * TEST BLOCK
     * CREATE TABLE MANAGER TESTS
     */

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableManagerTest() {
        testManagerCreator.create(null);
    }
}
