package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.shell.RemoveCommand;
import ru.fizteh.fivt.students.abramova.shell.Stage;
import ru.fizteh.fivt.students.abramova.shell.Status;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MultiFileMap {
    private final String workingDirectory;
    private Set<String> tables = new HashSet<String>();
    private TableMultiFile workingTable = null;

    public MultiFileMap(String directory) throws IOException {
        workingDirectory = directory != null ? directory : new File("").getCanonicalPath();
        File dir = new File(workingDirectory);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException("Cannot create directory with properties: " + directory);
            }
        } else {
            for (File subdir : dir.listFiles()) {
                if (subdir.isDirectory()) {
                    tables.add(subdir.getName());
                }
            }
        }
    }

    public TableMultiFile getWorkingTable() {
        return workingTable;
    }

    //Возвращает null, если переданной таблицы не существует, и текущую таблицу иначе
    public TableMultiFile setWorkingTable(String table) throws IOException {
        if (!containsTable(table)) {
            return null;
        }
        if (workingTable != null) {
            workingTable.close();
        }
        workingTable = new TableMultiFile(table, workingDirectory);
        return workingTable;
    }

    //Возвращает true, если таблица добавлена и не существовала ранее, иначе false
    public boolean addTable(String table) throws IOException {
        if (tables.contains(table)) {
            return false;
        }
        if (!new File(workingDirectory, table).mkdir()) {
            throw new IOException("Can not create table " + table);
        }
        tables.add(table);
        return true;
    }

    public void removeTable(String table) throws IOException {
        if (workingTable != null && workingTable.getTableName().equals(table)) {
            workingTable = null;
        }
        tables.remove(table);
        //Удаляем с помощью команды rm из шелла
        String[] args = new String[1];
        args[0] = table;
        new RemoveCommand(workingDirectory).doCommand(args, new Status(new Stage(workingDirectory)));
    }

    public boolean containsTable(String table) {
        return tables.contains(table);
    }

    public void closeWorkingTable() throws IOException {
        if (workingTable != null) {
            workingTable.close();
        }
    }
}
