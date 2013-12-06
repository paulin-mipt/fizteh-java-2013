package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandRemove extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        Table table = manager.getCurrentTable();
        if (table == null) {
            manager.printMessage("no table");
            return false;
        }
        Storeable oldValue = table.remove(args[1]);
        if (oldValue == null) {
            manager.printMessage("not found");
        } else {
            manager.printMessage("removed");
        }
        return true;
    }

    public TableCommandRemove(TableManager tableManager) {
        super("remove", 2);
        manager = tableManager;
    }
}
