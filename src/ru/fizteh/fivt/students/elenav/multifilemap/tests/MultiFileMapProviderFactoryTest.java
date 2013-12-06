package ru.fizteh.fivt.students.elenav.multifilemap.tests;

import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapProviderFactory;

public class MultiFileMapProviderFactoryTest {
    
    private TableProviderFactory factory;
    
    @Before
    public void init() {
        factory = new MultiFileMapProviderFactory();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        factory.create(null);
    }
}
