package ru.fizteh.fivt.students.eltyshev.filemap;

import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapReader;
import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapWriter;
import ru.fizteh.fivt.students.eltyshev.filemap.base.SimpleTableBuilder;
import ru.fizteh.fivt.students.eltyshev.filemap.base.StringTable;
import ru.fizteh.fivt.students.eltyshev.multifilemap.DatabaseFileDescriptor;

import java.io.File;
import java.io.IOException;

public class SingleFileTable extends StringTable {

    private static final String DATABASE_FILE_NAME = "db.dat";

    public SingleFileTable(String directory, String tableName) {
        super(directory, tableName);
    }

    @Override
    protected DatabaseFileDescriptor makeDescriptor(String s) {
        return new DatabaseFileDescriptor(-1, -1);
    }

    protected void load() throws IOException {
        FilemapReader.loadFromFile(getDatabaseFilePath(), new SimpleTableBuilder(this));
    }

    protected void save() throws IOException {
        FilemapWriter.saveToFile(getDatabaseFilePath(), oldData.keySet(), new SimpleTableBuilder(this));
    }

    private String getDatabaseFilePath() {
        File databaseFile = new File(getDatabaseDirectory(), DATABASE_FILE_NAME);
        return databaseFile.getAbsolutePath();
    }


}
