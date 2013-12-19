package ru.fizteh.fivt.students.kochetovnicolai.fileMap.tableCommands;

import ru.fizteh.fivt.students.kochetovnicolai.fileMap.TableManager;
import ru.fizteh.fivt.students.kochetovnicolai.fileMap.TableRecord;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

import java.util.ArrayList;

public class TableCommandCreate extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        if (manager.existsTable(args[1])) {
            manager.printMessage(args[1] + " exists");
            return false;
        }
        try {
            ArrayList<Class<?>> types = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                String type = args[i];
                if (i == 2) {
                    type = type.substring(1);
                }
                if (i == args.length - 1) {
                    type = type.substring(0, type.length() - 1);
                }
                Class nextClass;
                types.add(nextClass = TableRecord.SUPPORTED_TYPES.get(type));
                if (nextClass == null) {
                    manager.printMessage("wrong type (" + type + ")");
                    return false;
                }
            }
            manager.createTable(args[1], types);
            manager.printMessage("created");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TableCommandCreate(TableManager tableManager) {
        super("create", -3);
        manager = tableManager;
    }
}
