package ru.fizteh.fivt.students.anastasyev.filemap.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTableProviderFactory;

import java.io.IOException;

public class FileMapTableProviderFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws IOException {
        TableProviderFactory factory = new FileMapTableProviderFactory();
        factory.create(null);
    }

}
