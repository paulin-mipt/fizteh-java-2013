package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class MyTableProviderFactoryTest {
    File createdFolder;
    TableProviderFactory tempFactory;
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void initTempTableProvider() throws IOException {
        createdFolder = tempFolder.newFolder("workFolder");
        tempFactory = new MyTableProviderFactory();
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void dirIsEmpty() throws IOException {
        tempFactory.create("");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void dirIsNull() throws IOException {
        tempFactory.create(null);
    }
    
    @Test (expected = IOException.class)
    public void dirNotExists() throws IOException {
        tempFactory.create("notExistsDir");
    }
    
    @Test
    public void dirIsCorrect() throws IOException {
        TableProvider createdTableProvider = null;    
        createdTableProvider = tempFactory.create(createdFolder.toString());
        assertNotNull("Object of Table Provider shouldn't be null", createdTableProvider);
    }
}
