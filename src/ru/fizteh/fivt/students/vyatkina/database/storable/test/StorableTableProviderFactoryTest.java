package ru.fizteh.fivt.students.vyatkina.database.storable.test;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.vyatkina.database.storable.StorableTableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ru.fizteh.fivt.students.vyatkina.database.superior.SuperTableProviderFactory.DATABASE_IS_NOT_A_DIRECTORY;
import static ru.fizteh.fivt.students.vyatkina.database.superior.SuperTableProviderFactory.EMPTY_DIRECTORY;
import static ru.fizteh.fivt.students.vyatkina.database.superior.SuperTableProviderFactory.NULL_DIRECTORY;

public class StorableTableProviderFactoryTest {

    private static final String NEW_DIRECTORY = "fresh-new-directory";
    private static final String SOME_UNEXPECTED_FILE = "confused-file";

    private StorableTableProviderFactory factory = new StorableTableProviderFactory();
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createTableProviderShouldNotFail() throws IOException {
        factory.create(folder.getRoot().toString());
    }

    @Test
    public void createTableProviderInNullDirectoryShouldFail() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(NULL_DIRECTORY);
        factory.create(null);
    }

    @Test
    public void createTableProviderInEmptyDirectoryShouldFail() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(EMPTY_DIRECTORY);
        factory.create("");
    }

    @Test
    public void createTableProviderInNotExistingDirectoryShouldCreateThisDirectory() throws IOException {
        Path newDirectory = folder.getRoot().toPath().resolve(NEW_DIRECTORY);
        factory.create(newDirectory.toString());
        Assert.assertTrue("New directory should be created", Files.exists(newDirectory));
    }

    @Test
    public void createTableProviderInFileShouldFail() throws IOException {
        Path newFile = folder.getRoot().toPath().resolve(SOME_UNEXPECTED_FILE);
        Files.createFile(newFile);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(DATABASE_IS_NOT_A_DIRECTORY);
        factory.create(newFile.toString());
    }
}
