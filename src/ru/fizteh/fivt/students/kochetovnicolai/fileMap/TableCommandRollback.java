package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandRollback extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        Table table = manager.getCurrentTable();
        if (table == null) {
            manager.printMessage("no table");
            return false;
        }
        manager.printMessage(Integer.toString(table.rollback()));
        return true;
    }

    public TableCommandRollback(TableManager tableManager) {
        super("rollback", 1);
        manager = tableManager;
    }
}
