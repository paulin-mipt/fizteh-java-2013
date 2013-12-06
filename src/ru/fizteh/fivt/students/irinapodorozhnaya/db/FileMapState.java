package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandExit;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.State;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.FileStorage;

public class FileMapState extends State implements DbState {

    private Map<String, String> data;
    private File dbFile;
    
    public FileMapState(InputStream in, PrintStream out) throws  IOException {
        super(in, out);
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            throw new IOException("can't get property");
        }
        dbFile = new File(path, "db.dat");
        data = FileStorage.openDataFile(dbFile, -1);
        if (data == null) {
            data = new HashMap<>();                    
        }
        add(new CommandExit(this));
        add(new CommandPut(this));
        add(new CommandRemove(this));
        add(new CommandGet(this));
    }
    
    @Override
    public String getValue(String key) throws IOException {
        return data.get(key);
    }

    @Override
    public String removeValue(String key) throws IOException {
        return data.remove(key);
    }

    @Override
    public String put(String key, String value) throws IOException {
        return data.put(key, value);
    }
    
    @Override
    public int commitDif() throws IOException {
        FileStorage.commitDiff(dbFile, data);
        return 0;
    }
}
