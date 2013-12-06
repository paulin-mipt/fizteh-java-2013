package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.*;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapProviderFactory;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.ErrorShell;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFileMapProviderFactory {

    private FileMapProviderFactory multiMapFactory;
    private CommandShell mySystem;
    private Path pathTables;

    @Before
    public void setUp() {

        pathTables = Paths.get(".");
        mySystem = new CommandShell(pathTables.toString(), false, false);
        pathTables = pathTables.resolve("bdTest");
        try {
            mySystem.mkdir(new String[]{pathTables.toString()});
        } catch (ErrorShell e) {
            e.printStackTrace();
        }

        try {
            multiMapFactory = new FileMapProviderFactory();
        } catch (Exception e) {
            e.printStackTrace();
            FileMapUtils.getMessage(e);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull() throws IOException {
        multiMapFactory.create(null);
    }

    @Test(expected = IOException.class)
    public void createNotExist() throws IOException {
        multiMapFactory.create("/123123/1243");
    }

    @Test()
    public void createProvider() throws IOException {
        multiMapFactory.create(pathTables.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableProviderCallCreate() throws IOException {
        multiMapFactory.close();
        multiMapFactory.create("213");
    }

    @After
    public void tearDown() {
        multiMapFactory.close();
        try {
            mySystem.rm(new String[]{pathTables.toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

