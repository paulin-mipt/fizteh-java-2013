package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

import java.text.ParseException;

public class TableCommandPut extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        Table table = manager.getCurrentTable();
        if (table == null) {
            manager.printMessage("no table");
            return false;
        }
        Storeable storeable;
        try {
            storeable = manager.deserialize(args[2]);
        } catch (ParseException e) {
            manager.printMessage("wrong type (" + e.getMessage() + ")");
            return false;
        }
        Storeable oldValue = table.put(args[1], storeable);
        String oldString;
        try {
            oldString = manager.serialize(oldValue);
        } catch (ParseException e) {
            manager.printMessage("wrong type (" + e.getMessage() + ")");
            return false;
        }
        if (oldValue == null) {
            manager.printMessage("new");
        } else {
            manager.printMessage("overwrite");
            manager.printMessage(oldString);
        }
        return true;
    }

    public TableCommandPut(TableManager tableManager) {
        super("put", 3);
        manager = tableManager;
    }
}
