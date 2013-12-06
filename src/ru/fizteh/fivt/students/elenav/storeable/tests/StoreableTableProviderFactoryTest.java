package ru.fizteh.fivt.students.elenav.storeable.tests;

import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.elenav.storeable.StoreableTableProviderFactory;

public class StoreableTableProviderFactoryTest {
    
        private TableProviderFactory factory;

        @Before
        public void init() throws Exception {
            factory = new StoreableTableProviderFactory();
        }

        @Test(expected = IllegalArgumentException.class)
        public void testCreateNullPath() throws Exception {
            factory.create(null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testCreateEmptyPath() throws Exception  {
            factory.create("   ");
        }
}
