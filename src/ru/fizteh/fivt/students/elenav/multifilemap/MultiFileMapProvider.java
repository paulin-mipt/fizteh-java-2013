package ru.fizteh.fivt.students.elenav.multifilemap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.states.Provider;
import ru.fizteh.fivt.students.elenav.storeable.StoreableTableState;
import ru.fizteh.fivt.students.elenav.utils.Functions;

public class MultiFileMapProvider implements TableProvider, Provider {
    
    private File workingDirectory = null;
    private PrintStream stream;
    public HashMap<String, MultiFileMapState> tables = new HashMap<>();
    
    public MultiFileMapProvider(File db, PrintStream s) {
        if (db == null) {
            throw new IllegalArgumentException("can't create provider: null name");
        }
        if (!db.isDirectory()) {
            throw new IllegalArgumentException("can't create provider: name is file or name doesn't exist");
        }
        for (File f : db.listFiles()) {
            if (f.isDirectory()) {
                tables.put(f.getName(), new MultiFileMapState(f.getName(), f, getStream()));
            }
        }
        setWorkingDirectory(db);
        setStream(s);
    }

    private boolean chechIsNameInvalid(String name) {
        name = name.trim();
        return name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".") || name.contains(" ");
    }
    
    @Override
    public MultiFileMapState getTable(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("can't get table with null name");
        }
        if (chechIsNameInvalid(name)) {
            throw new RuntimeException("can't get table with invalid name");
        }
        return tables.get(name);
    }
    
    @Override
    public MultiFileMapState createTable(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("can't create table with null name");
        }
        if (chechIsNameInvalid(name)) {
            throw new RuntimeException("can't create table with invalid name");
        }
        File f = new File(getWorkingDirectory(), name);
        if (f.exists()) {
            return null;
        }
        f.mkdir();
        MultiFileMapState table = new MultiFileMapState(name, f, getStream());
        tables.put(name, table);
        return table;
    }
    
    @Override
    public void removeTable(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("can't remove table: invalid name");
        }
        if (tables.get(name) == null) {
            throw new IllegalStateException("can't remove table: table not exist");
        }
        if (chechIsNameInvalid(name)) {
            throw new RuntimeException("can't remove table with invalid name");
        }
        try {
            Functions.deleteRecursively(tables.get(name).getWorkingDirectory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tables.remove(name);
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public PrintStream getStream() {
        return stream;
    }

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public StoreableTableState createTable(String string, List<Class<?>> identifyTypes) {
        System.err.print("Command can't be executed");
        return null;
    }

    @Override
    public void use(FilesystemState s) throws IOException {
        
    }

}
