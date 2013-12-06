package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.junittests;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileProviderFactory;

public class MultiFileProviderFactoryTest {
    private static final String TESTED_DIRECTORY = "/home/hope/JavaTests";
    TableProviderFactory tableProviderFactory;

    @Before
    public void setUp() throws Exception {
        tableProviderFactory = new MultiFileProviderFactory();
    }

    @Test
    public void createValidTest() throws Exception {
        Assert.assertNotNull(tableProviderFactory.create(TESTED_DIRECTORY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullNameShouldFail() {
        tableProviderFactory.create(null);
    }

}
