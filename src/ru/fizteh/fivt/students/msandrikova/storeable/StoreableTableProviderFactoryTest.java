package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class StoreableTableProviderFactoryTest {
    StoreableTableProviderFactory tableProviderFactory;

    @Before
    public void setUp() throws Exception {
        tableProviderFactory = new StoreableTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() throws IOException {
        tableProviderFactory.create(null);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testClose() throws IllegalStateException, IllegalArgumentException, IOException {
        this.tableProviderFactory.close();
        this.tableProviderFactory.create(".");
    }

}
