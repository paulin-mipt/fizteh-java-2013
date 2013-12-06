package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.CommandUtils;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalTableProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapProvider implements TableProvider, UniversalTableProvider {
    public static final String TABLE_NAME = "[a-zA-Zа-яА-Я0-9]+";
    private File workingDirectory;
    public MultiFileDataTable curDataBaseStorage = null;
    private Map<String, MultiFileDataTable> dataBaseTables = new HashMap<String, MultiFileDataTable>();


    public MultiFileHashMapProvider(File dir) throws IOException {
        workingDirectory = dir;
        File[] tables = workingDirectory.listFiles();
        if (tables.length != 0) {
            for (File f : tables) {
                if (f.isDirectory()) {
                    MultiFileDataTable dataTable = new MultiFileDataTable(f.getName(), workingDirectory);
                    dataBaseTables.put(f.getName(), dataTable);
                }
            }
        }
    }


    public MultiFileDataTable setCurTable(String newTable) throws IOException {
        try {
            MultiFileDataTable dataTable = null;
            if (!dataBaseTables.isEmpty()) {
                dataTable = dataBaseTables.get(newTable);
                if (dataTable != null) {
                    dataTable.load();
                    if (curDataBaseStorage != null) {
                        curDataBaseStorage.writeToDataBase();
                    }
                    curDataBaseStorage = dataTable;
                }
            }
            return dataTable;
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }


    public MultiFileDataTable getTable(String name) throws IllegalArgumentException {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        return dataBaseTables.get(name);
    }

    public Table createTable(String name) throws IllegalArgumentException {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        if (dataBaseTables.get(name) != null) {
            return null;
        } else {
            File newTableFile = new File(workingDirectory, name);
            try {
                newTableFile = newTableFile.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Programme's mistake in getting canonical file");
            }
            newTableFile.mkdir();
            MultiFileDataTable newTable = new MultiFileDataTable(name, workingDirectory);
            dataBaseTables.put(name, newTable);
            return newTable;
        }
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        if (dataBaseTables.get(name) != null) {
            File table = new File(workingDirectory, name);
            try {
                table = table.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Programme's mistake in getting canonical file");
            }
            try {
                CommandUtils.recDeletion(table);
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            dataBaseTables.remove(name);

        } else {
            throw new IllegalStateException(name + "not exists");
        }
    }

    public UniversalDataTable getCurTable() {
        return curDataBaseStorage;
    }
}


