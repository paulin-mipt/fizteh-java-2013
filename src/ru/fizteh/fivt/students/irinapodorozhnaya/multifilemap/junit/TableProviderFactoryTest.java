package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.junit;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MyTableProviderFactory;

public class TableProviderFactoryTest {
    private TableProviderFactory factory;
    private static final String DATA_BASE_DIR = "./src/ru/fizteh/fivt/students/irinapodorozhnaya/test";
    
    @Before
    public void setUp() throws Exception {
        new File(DATA_BASE_DIR).mkdirs();
        factory = new MyTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateIllegal() throws Exception {
        factory.create("%*&YIH&");
    }
    
    @Test
    public void testCreateLegal() throws Exception {
        Assert.assertNotNull(factory.create(DATA_BASE_DIR));
    }
}
