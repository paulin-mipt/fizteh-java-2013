package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;

import org.junit.*;

public class TableProviderFactoryTest {
    private NewTableProviderFactory factory = new NewTableProviderFactory();

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws IOException {
        factory.create(null);
    }

}
