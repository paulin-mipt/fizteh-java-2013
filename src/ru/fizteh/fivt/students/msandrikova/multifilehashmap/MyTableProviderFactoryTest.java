package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import org.junit.Before;
import org.junit.Test;

public class MyTableProviderFactoryTest {
    MyTableProviderFactory tableProviderFactory;

    @Before
    public void setUp() throws Exception {
        tableProviderFactory = new MyTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() {
        tableProviderFactory.create(null);
    }

}
