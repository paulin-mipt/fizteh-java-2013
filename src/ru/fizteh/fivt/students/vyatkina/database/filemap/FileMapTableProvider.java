package ru.fizteh.fivt.students.vyatkina.database.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.StringTableProvider;
import ru.fizteh.fivt.students.vyatkina.database.superior.DatabaseUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.dbDirPropertyCheck;

public class FileMapTableProvider implements StringTableProvider {

    private Path location;
    private Path fileName;
    FileMapTable table;

    public FileMapTableProvider(FileMapTable table) {
        this.table = table;
        location = Paths.get(dbDirPropertyCheck());
        fileName = location.resolve("db.dat");
        loadDatabase();
    }

    public void loadDatabase() {

        if (Files.notExists(fileName)) {
            return;
        }

        try (DataInputStream in = new DataInputStream(new BufferedInputStream
                (new FileInputStream(fileName.toFile())))) {

            while (in.available() != 0) {
                DatabaseUtils.KeyValue pair = DatabaseUtils.readKeyValue(in);
                table.putValueFromDisk(pair.key, pair.value);
            }
        }

        catch (IllegalArgumentException | IOException e) {
            throw new WrappedIOException("Unable to read from file: " + e.getMessage());
        }

    }

    @Override
    public void saveChangesOnExit() {
        if (table.getKeysThatValuesHaveChanged() == null) {
            return;
        }
        try {
            Files.deleteIfExists(fileName);
            Files.createFile(fileName);
        }
        catch (IOException e) {
            throw new WrappedIOException(e.getMessage());
        }

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream
                (new FileOutputStream(fileName.toFile(), true)))) {

            for (String key : table.getKeys()) {
                String value = table.get(key);
                DatabaseUtils.writeKeyValue(new DatabaseUtils.KeyValue(key, value), out);
            }
        }
        catch (IOException e) {
            throw new WrappedIOException("Unable to write to file: " + e.getMessage());
        }
    }

    @Override
    public Table getTable(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Table createTable(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeTable(String name) {
        throw new UnsupportedOperationException();
    }
}
