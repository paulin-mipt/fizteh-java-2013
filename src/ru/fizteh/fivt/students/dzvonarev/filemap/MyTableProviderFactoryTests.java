package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.Test;

import java.io.IOException;

public class MyTableProviderFactoryTests {

    @Test(expected = IllegalArgumentException.class)
    public void createTableProviderWithNullParameter() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        factory.create(null);
    }

    @Test
    public void createNotExisting() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        factory.create("newDir");
    }
}
