package ru.fizteh.fivt.students.kochetovnicolai.fileMap.tableCommands;

import ru.fizteh.fivt.students.kochetovnicolai.fileMap.DistributedTable;
import ru.fizteh.fivt.students.kochetovnicolai.fileMap.TableManager;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandUse extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        if (!manager.existsTable(args[1])) {
            manager.printMessage(args[1] + " not exists");
            return false;
        }
        try {
            DistributedTable table = manager.getCurrentTable();
            if (table != null && table.changesSize() != 0) {
                manager.printMessage(Integer.toString(table.changesSize()) + " unsaved changes");
                return false;
            }
            manager.setCurrentTable(manager.getTable(args[1]));
            manager.printMessage("using " + args[1]);
            return true;
        } catch (IllegalArgumentException e) {
            manager.printMessage(e.getMessage());
            return false;
        }
    }

    public TableCommandUse(TableManager tableManager) {
        super("use", 2);
        manager = tableManager;
    }
}
