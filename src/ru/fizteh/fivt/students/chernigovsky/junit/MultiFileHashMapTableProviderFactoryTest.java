package ru.fizteh.fivt.students.chernigovsky.junit;

import org.junit.*;

public class MultiFileHashMapTableProviderFactoryTest {
    private MultiFileHashMapTableProviderFactory tableProviderFactory;

    @Before
    public void setUp() {
        tableProviderFactory = new MultiFileHashMapTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNlShouldFail() {
        tableProviderFactory.create(null);
    }
}