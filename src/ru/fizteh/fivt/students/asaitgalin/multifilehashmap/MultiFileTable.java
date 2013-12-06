package ru.fizteh.fivt.students.asaitgalin.multifilehashmap;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container.TableContainer;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.extensions.ChangesCountingTable;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.values.TableValuePackerString;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.values.TableValueUnpackerString;

import java.io.File;
import java.io.IOException;

public class MultiFileTable implements ChangesCountingTable {
    // Underlying container
    private TableContainer<String> container;

    private File tableDir;
    private String name;

    public MultiFileTable(File tableDir, String name) {
        this.name = name;
        this.tableDir = tableDir;
        this.container = new TableContainer<String>(tableDir, new TableValuePackerString(),
                new TableValueUnpackerString());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("get: key is null");
        }
        return container.containerGetValue(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put: key or value is null");
        }
        if (key.trim().isEmpty() || value.trim().isEmpty()) {
            throw new IllegalArgumentException("put: key or value is empty");
        }
        return container.containerPutValue(key, value);
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key is null");
        }
        return container.containerRemoveValue(key);
    }

    @Override
    public int size() {
        return container.containerGetSize();
    }

    @Override
    public int commit() {
        try {
            return container.containerCommit();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public int rollback() {
        return container.containerRollback();
    }

    @Override
    public int getChangesCount() {
        return container.containerGetChangesCount();
    }

    public void load() throws IOException {
        container.containerLoad();
    }

}

