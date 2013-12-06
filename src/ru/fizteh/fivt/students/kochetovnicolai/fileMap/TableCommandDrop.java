package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandDrop extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        if (!manager.existsTable(args[1])) {
            manager.printMessage(args[1] + " not exists");
            return false;
        }
        try {
            if (manager.removeTable(args[1])) {
                manager.printMessage("dropped");
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TableCommandDrop(TableManager tableManager) {
        super("drop", 2);
        manager = tableManager;
    }
}
