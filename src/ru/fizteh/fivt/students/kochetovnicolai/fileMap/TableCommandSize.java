package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandSize extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        Table table = manager.getCurrentTable();
        if (table == null) {
            manager.printMessage("no table");
            return false;
        }
        manager.printMessage(Integer.toString(table.size()));
        return true;
    }

    public TableCommandSize(TableManager tableManager) {
        super("size", 1);
        manager = tableManager;
    }
}
