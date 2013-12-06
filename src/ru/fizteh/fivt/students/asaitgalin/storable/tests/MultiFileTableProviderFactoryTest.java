package ru.fizteh.fivt.students.asaitgalin.storable.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.asaitgalin.storable.MultiFileTableProviderFactory;

import java.io.IOException;

public class MultiFileTableProviderFactoryTest {
    private static final String BAD_PATH = "/notexistingdir";
    private TableProviderFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new MultiFileTableProviderFactory();
    }

    @Test(expected = IOException.class)
    public void testCreateProviderUnavailable() throws Exception {
        factory.create(BAD_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() throws Exception {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithSpaces() throws Exception  {
        factory.create("   ");
    }
}
