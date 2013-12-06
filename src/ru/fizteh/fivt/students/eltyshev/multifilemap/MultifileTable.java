package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.students.eltyshev.filemap.base.SimpleTableBuilder;
import ru.fizteh.fivt.students.eltyshev.filemap.base.StringTable;

import java.io.File;
import java.io.IOException;

public class MultifileTable extends StringTable {

    public MultifileTable(String directory, String tableName) {
        super(directory, tableName);
    }

    @Override
    protected DatabaseFileDescriptor makeDescriptor(String key) {
        return MultifileMapUtils.makeDescriptor(key);
    }

    protected void save() throws IOException {
        DistributedSaver.save(new SimpleTableBuilder(this), getChangedFiles());
    }

    protected void load() throws IOException {
        DistributedLoader.load(new SimpleTableBuilder(this));
    }

    private File getTableDirectory() {
        File tableDirectory = new File(getDatabaseDirectory(), getName());
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
        }
        return tableDirectory;
    }
}