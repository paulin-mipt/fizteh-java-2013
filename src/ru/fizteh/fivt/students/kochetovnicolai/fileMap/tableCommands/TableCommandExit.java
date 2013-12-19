package ru.fizteh.fivt.students.kochetovnicolai.fileMap.tableCommands;

import ru.fizteh.fivt.students.kochetovnicolai.fileMap.TableManager;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandExit extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        manager.setExit();
        return true;
    }

    public TableCommandExit(TableManager tableManager) {
        super("exit", 1);
        manager = tableManager;
    }
}
