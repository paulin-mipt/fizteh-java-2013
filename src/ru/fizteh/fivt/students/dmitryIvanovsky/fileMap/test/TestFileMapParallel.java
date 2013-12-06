package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapProviderFactory;
import java.io.File;

public class TestFileMapParallel {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private File mainDir;
    private File tableFile;
    private TableProviderFactory factory;
    private TableProvider provider;
    private Table table;

    @Before
    public void before() {
        try {
            mainDir = tmpFolder.newFolder("testTable");
            factory = new FileMapProviderFactory();
            tableFile = new File(mainDir, "testTable");
            provider = factory.create(mainDir.toString());
            table = provider.getTable("testTable");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        tmpFolder.delete();
    }

    @Test
    public void multiCreate() {

    }
}
