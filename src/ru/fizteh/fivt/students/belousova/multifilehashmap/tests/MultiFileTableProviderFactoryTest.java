package ru.fizteh.fivt.students.belousova.multifilehashmap.tests;

import junit.framework.Assert;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.belousova.multifilehashmap.MultiFileTableProviderFactory;

public class MultiFileTableProviderFactoryTest {
    private TableProviderFactory tableProviderFactory = new MultiFileTableProviderFactory();

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        tableProviderFactory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmpty() throws Exception {
        tableProviderFactory.create("");
    }

    @Test
    public void testCreateNotExisted() throws Exception {
        Assert.assertNotNull(tableProviderFactory.create("newNotExistedTable"));
    }
}
