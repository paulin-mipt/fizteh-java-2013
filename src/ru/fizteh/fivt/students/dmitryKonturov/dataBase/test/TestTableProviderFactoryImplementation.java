package ru.fizteh.fivt.students.dmitryKonturov.dataBase.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableProviderFactoryImplementation;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class TestTableProviderFactoryImplementation {
    private TableProviderFactoryImplementation testFactory;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void initTempFolderProvider() throws IOException {
        testFactory = new TableProviderFactoryImplementation();
    }

    @Test (expected = IllegalArgumentException.class)
    public void dirIsNull() throws IOException {
        testFactory.create(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void dirIsEmpty() throws IOException {
        testFactory.create("");
    }

    @Test (expected = IOException.class)
    public void dirNotExists() throws IOException {
        testFactory.create("notExistingDirectory");
    }

    @Test
    public void dirCorrectCreate() throws IOException {
        File existingFolder = folder.newFolder("existingFolder");
        TableProvider provider;
        provider = testFactory.create(existingFolder.toString());
        assertNotNull("Provider for existing empty folder should not be null",
                      provider);
    }

}
