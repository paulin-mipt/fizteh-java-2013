package ru.fizteh.fivt.students.kochetovnicolai.fileMap.tableCommands;

import ru.fizteh.fivt.students.kochetovnicolai.fileMap.TableManager;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandStopHttp extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        return manager.stopHTTP();
    }

    public TableCommandStopHttp(TableManager tableManager) {
        super("stophttp", 1);
        manager = tableManager;
    }
}
