package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.junittests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileProviderFactory;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.CommandUtils;
import ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.StoreableTableProviderFactory;

import java.io.File;

public class StoreableTableProviderFactoryTest {
    private static final String TESTED_DIRECTORY = "JavaTests";
    TableProviderFactory tableProviderFactory;

    @Before
    public void setUp() throws Exception {
        tableProviderFactory = new StoreableTableProviderFactory();
    }

    @After
    public void tearDown() throws Exception {
        File file = new File(TESTED_DIRECTORY);
        if (file.exists()) {
            CommandUtils.recDeletion(file);
        }
    }

    @Test
    public void createValidTest() throws Exception {
        Assert.assertNotNull(tableProviderFactory.create(TESTED_DIRECTORY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullNameShouldFail() throws Exception {
        tableProviderFactory.create(null);
    }
}
