package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableProviderFactory;

public class MultiFileTableProviderFactoryTest {
    MultiFileTableProviderFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new MultiFileTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() throws Exception {
        factory.create(null);
    }
}
