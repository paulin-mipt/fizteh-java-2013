package ru.fizteh.fivt.students.piakovenko.filemap.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.piakovenko.filemap.*;
import ru.fizteh.fivt.students.piakovenko.filemap.storable.JSON.JSONSerializer;
import ru.fizteh.fivt.students.piakovenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 19.10.13
 * Time: 20:17
 * To change this template use File | Settings | File Templates.
 */
public class DataBasesCommander implements TableProvider {
    private File dataBaseDirectory = null;
    private DataBase currentDataBase = null;
    private Map<String, DataBase> filesMap = new HashMap<String, DataBase>();
    private Shell shell = null;
    private GlobalFileMapState state = null;;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private static File getMode(File directory) {
        for (File f: directory.listFiles()) {
            if (f.getName().equals("db.dat") && f.isFile()) {
                return f;
            }
        }
        return null;
    }

    private void fulfillFiles() {
        for (File f: dataBaseDirectory.listFiles()) {
            filesMap.put(f.getName(), new DataBase(shell, f, this));
        }
    }

    public DataBasesCommander(Shell s, File storage) {
        shell = s;
        dataBaseDirectory = storage;
        File modeFile = null;
        if ((modeFile = getMode(storage)) != null) {
            currentDataBase = new DataBase(shell, modeFile, this);
            state  = new GlobalFileMapState(currentDataBase, this);
            currentDataBase.initialize(state);
            shell.changeInvitation(" $ ");
        } else {
            fulfillFiles();
            state  = new GlobalFileMapState(currentDataBase, this);
            initialize(state);
            shell.changeInvitation(" $ ");
        }
    }

    public void use(String dataBase) throws IOException {
        if (filesMap.containsKey(dataBase)) {
            if (currentDataBase != null && currentDataBase.numberOfChanges() != 0) {
                System.out.println(currentDataBase.numberOfChanges() + " unsaved changes");
                return;
            }
            if (filesMap.get(dataBase).equals(currentDataBase)) {
                System.out.println("using " + dataBase);
                return;
            }  else if (currentDataBase != null) {
                currentDataBase.saveDataBase();
            }
            currentDataBase = filesMap.get(dataBase);
            currentDataBase.load();
            state.changeTable(currentDataBase);
            System.out.println("using " + dataBase);
        } else {
            System.out.println(dataBase + " not exists");
        }
    }

    @Override
    public void removeTable(String dataBase) throws IllegalArgumentException, IOException {
        if (dataBase == null || dataBase.trim().equals("")) {
            throw new IllegalArgumentException("Null pointer to dataBase name");
        }
        try {
            readWriteLock.writeLock().lock();
            if (filesMap.containsKey(dataBase)) {
                if (filesMap.get(dataBase).equals(currentDataBase)) {
                    currentDataBase = null;
                    state.changeTable(currentDataBase);
                }
                try {
                    ru.fizteh.fivt.students.piakovenko.shell.Remove.removeRecursively(
                            filesMap.get(dataBase).returnFiledirectory());
                } catch (IOException e) {
                    System.err.println("Error! " + e.getMessage());
                    System.exit(1);
                }
                filesMap.remove(dataBase);
                System.out.println("dropped");
            } else {
                System.out.println(dataBase + " not exists");
                throw new IllegalStateException(dataBase + " not exists");
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException {
        Checker.stringNotEmpty(name);
        Checker.correctTableName(name);
        Checker.checkColumnTypes(columnTypes);
        try {
            readWriteLock.writeLock().lock();
            if (filesMap.containsKey(name)) {
                System.out.println(name + " exists");
            } else {
                File newFileMap = new File(dataBaseDirectory, name);
                if (newFileMap.isFile()) {
                    throw new IllegalArgumentException("try create table on file");
                }
                if (!newFileMap.mkdirs()) {
                    System.err.println("Unable to create this directory - " + name);
                    System.exit(1);
                }
                System.out.println("created");
                filesMap.put(name, new DataBase(shell, newFileMap, this, columnTypes));
                return filesMap.get(name);
            }
            return null;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        Checker.stringNotEmpty(name);
        Checker.correctTableName(name);
        try {
            readWriteLock.readLock().lock();
            if (filesMap.containsKey(name)) {
                return filesMap.get(name);
            } else {
                return null;
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public Storeable deserialize(Table table, String value) throws ParseException {
        return JSONSerializer.deserialize(table, value);
    }

    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return JSONSerializer.serialize(table, value);
    }

    public Storeable createFor(Table table) {
        List<Class<?>> typesList = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            typesList.add(table.getColumnType(i));
        }
        return new Element(typesList);
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        Checker.equalSizes(values.size(), table.getColumnsCount());
        List<Class<?>> typesList = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            typesList.add(table.getColumnType(i));
        }
        Storeable result = new Element(typesList);
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            result.setColumnAt(i, values.get(i));
        }
        return result;
    }

    public void initialize(GlobalFileMapState state) {
        shell.addCommand(new Exit(state));
        shell.addCommand(new Drop(state));
        shell.addCommand(new Create(state));
        shell.addCommand(new Use(state));
        shell.addCommand(new Get(state));
        shell.addCommand(new Put(state));
        shell.addCommand(new Remove(state));
        shell.addCommand(new Size(state));
        shell.addCommand(new Commit(state));
        shell.addCommand(new Rollback(state));
    }
}
