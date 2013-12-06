package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TestsForTableProviderFactory {
    TableProviderFactory tableProviderFactory;
    
    @Before
    public void createTable() throws IOException {
        tableProviderFactory = new TableProviderFactoryImplementation();
    }
    
    @Test(expected = IOException.class)
    public void createNonExisting() throws IOException {
        tableProviderFactory.create("smth" + File.separator + "not-exists");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullDir() throws IOException {
        tableProviderFactory.create(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createEmptyDir() throws IOException {
        tableProviderFactory.create("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createWhitespaceDir() throws IOException {
        tableProviderFactory.create("\t \n  ");
    }
    
    @Test
    public void createDirWithBackSlash() throws IOException {
        tableProviderFactory.create("..\\table");
        Assert.assertTrue(Files.exists(Paths.get("..\\table")));
    }
    
}
