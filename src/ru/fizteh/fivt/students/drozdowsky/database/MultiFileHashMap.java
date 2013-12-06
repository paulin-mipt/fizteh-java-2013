package ru.fizteh.fivt.students.drozdowsky.database;

import ru.fizteh.fivt.students.drozdowsky.commands.ShellController;
import ru.fizteh.fivt.students.drozdowsky.PathController;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.drozdowsky.utils.Utils;

import java.awt.geom.IllegalPathStateException;
import java.io.File;
import java.util.HashMap;

public class MultiFileHashMap implements TableProvider {
    private File dir;
    private PathController curDir;
    private HashMap<String, FileHashMap> database;

    public MultiFileHashMap(String workingDir) {
        database = new HashMap<>();
        this.dir = new File(workingDir);
        if (!(dir.exists())) {
            throw new IllegalPathStateException();
        }
        String[] content = dir.list();
        for (String directory : content) {
            File temp = new File(dir.getAbsoluteFile() + File.separator + directory);
            FileHashMap base = new FileHashMap(temp);
            database.put(directory, null);
        }
        curDir = new PathController(dir.getAbsolutePath());
    }

    public FileHashMap getTable(String name) {
        if (!Utils.isValidTablename(name)) {
            throw new IllegalArgumentException();
        }
        if (database.containsKey(name)) {
            File table = new File(dir.getAbsolutePath() + File.separator + name);
            if (database.get(name) == null) {
                database.put(name, new FileHashMap(table));
            }
            return database.get(name);
        } else {
            return null;
        }
    }

    public FileHashMap createTable(String name) {
        if (!Utils.isValidTablename(name)) {
            throw new IllegalArgumentException();
        }
        if (database.containsKey(name)) {
            return null;
        } else {
            File newTable = new File(dir.getAbsolutePath() + File.separator + name);
            if (!newTable.mkdir()) {
                throw new IllegalPathStateException(newTable.getAbsolutePath() + ": Permission denied");
            }
            database.put(name, new FileHashMap(newTable));
            return database.get(name);
        }
    }

    public void removeTable(String name) {
        if (!Utils.isValidTablename(name)) {
            throw new IllegalArgumentException();
        }
        if (database.containsKey(name)) {
            ShellController t = new ShellController(curDir);
            t.rm(name);
            database.remove(name);

            database.remove(name);
        } else {
            throw new IllegalStateException();
        }
    }

    public void stopUsing(String name) {
        if (!Utils.isValidTablename(name)) {
            throw new IllegalArgumentException();
        }
        if (database.containsKey(name)) {
            database.put(name, null);
        } else {
            throw new IllegalStateException();
        }
    }
}
