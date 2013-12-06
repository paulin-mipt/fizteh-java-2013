package ru.fizteh.fivt.students.vorotilov.db;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class StoreableTableProviderFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void createWithIncorrectRootDir() {
        TableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        try {
            tableProviderFactory.create(null);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithEmptyRootDir() {
        TableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        try {
            tableProviderFactory.create(" ");
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Test(expected = IOException.class)
    public void createProviderUnavailableShouldFail() throws IOException {
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        tableProviderFactory.create(File.separator + "root" + File.separator + "abc");
    }

}
