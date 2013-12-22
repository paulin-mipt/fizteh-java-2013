package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.irinapodorozhnaya.db.CommandGet;
import ru.fizteh.fivt.students.irinapodorozhnaya.db.CommandPut;
import ru.fizteh.fivt.students.irinapodorozhnaya.db.CommandRemove;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandExit;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandCommit;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandDrop;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandRollBack;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandSize;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandUse;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.State;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MultiDbState;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendTable;

public class StoreableState extends State implements MultiDbState {
    private ExtendTable workingTable;
    private final ExtendProvider provider;
    
    public StoreableState(InputStream in, PrintStream out) throws IOException {
        super(in, out);
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            throw new IOException("can't get property");
        }
        File file = new File(path);

        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException("can't create directory");
            }
        }
        provider = new MyTableProviderFactory().create(path);
        
        add(new CommandUse(this));
        add(new CommandCreate(this));
        add(new CommandDrop(this));
        add(new CommandCommit(this));
        add(new CommandSize(this));
        add(new CommandRollBack(this));
        add(new CommandExit(this));
        add(new CommandPut(this));
        add(new CommandRemove(this));
        add(new CommandGet(this));
    }

    @Override
    public String getValue(String key) throws IOException {
        if (workingTable == null) {
            throw new IOException("no table");
        }

        return provider.serialize(workingTable, workingTable.get(key));
    }

    @Override
    public String removeValue(String key) throws IOException {
        if (workingTable == null) {
            throw new IOException("no table");
        }

        return provider.serialize(workingTable, workingTable.remove(key));
    }

    @Override
    public String put(String key, String value) throws IOException {
        if (workingTable == null) {
            throw new IOException("no table");
        }
        try {
            Storeable val = provider.deserialize(workingTable, value);
            return provider.serialize(workingTable, workingTable.put(key, val));
        } catch (ColumnFormatException | ParseException e) {
          throw new IOException("wrong type " + e.getMessage());
        }
    }
    
    @Override
    public int commitDif() throws IOException {
        if (workingTable != null) {
            return workingTable.commit();
        }
        return 0;
    }

    @Override
    public int getCurrentTableSize() throws IOException {
        if (workingTable != null) {
            return workingTable.size();
        } else {
            throw new IOException("no table");
        }
    }

    @Override
    public int rollBack() throws IOException {
        if (workingTable != null) {
            return workingTable.rollback();
        } else {
            throw new IOException("no table");
        }
    }
    
    @Override
    public void drop(String name) throws IOException {
        if (provider.getTable(name) == workingTable) {
            workingTable = null;
        }
        provider.removeTable(name);
    }

    @Override
    public void create(String name, List<Class<?>> columnType) throws IOException {
        try {
            if (provider.createTable(name, columnType) == null) {
                throw new IOException(name + " exists");
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("wrong type" + e.getMessage());
        }
    }
    
    @Override
    public void use(String name) throws IOException {
        try {
            ExtendTable table = provider.getTable(name);
            if (table == null) {
                throw new IOException(name + " not exists");
            }
            if (workingTable != null) {
                int n = workingTable.getChangedValuesNumber();
                if (n != 0) {
                    throw new IOException(n + " unsaved changes");
                }
            }
            this.workingTable = table;
        } catch (IllegalArgumentException e) {
            throw new IOException("illegal table name");
        }
    }
}
