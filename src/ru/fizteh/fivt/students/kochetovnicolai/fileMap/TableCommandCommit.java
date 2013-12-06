package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

import java.io.IOException;

public class TableCommandCommit extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        Table table = manager.getCurrentTable();
        if (table == null) {
            manager.printMessage("no table");
            return false;
        }
        try {
            manager.printMessage(Integer.toString(table.commit()));
        } catch (IOException e) {
            manager.printMessage(e.getMessage());
            return false;
        }
        return true;
    }

    public TableCommandCommit(TableManager tableManager) {
        super("commit", 1);
        manager = tableManager;
    }
}
