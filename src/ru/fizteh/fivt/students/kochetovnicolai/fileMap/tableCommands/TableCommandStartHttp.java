package ru.fizteh.fivt.students.kochetovnicolai.fileMap.tableCommands;

import ru.fizteh.fivt.students.kochetovnicolai.fileMap.TableManager;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandStartHttp extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        if (args.length > 2) {
            manager.printMessage(getName() + ": invalid number of arguments");
            return false;
        }
        int port = 10001;
        if (args.length == 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (ClassCastException e) {
                manager.printMessage("not started: " + args[1] + ": wrong port number");
                return false;
            }
        }
        return manager.startHTTP(port);
    }

    public TableCommandStartHttp(TableManager tableManager) {
        super("starthttp", -1);
        manager = tableManager;
    }
}
