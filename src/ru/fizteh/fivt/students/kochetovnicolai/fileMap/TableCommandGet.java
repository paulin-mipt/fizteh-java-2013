package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

import java.text.ParseException;

public class TableCommandGet extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        Table table = manager.getCurrentTable();
        if (table == null) {
            manager.printMessage("no table");
            return false;
        }
        Storeable value = table.get(args[1]);
        if (value == null) {
            manager.printMessage("not found");
        } else {
            manager.printMessage("found");
            String stringValue;
            try {
                stringValue = manager.serialize(table.get(args[1]));
            } catch (ParseException e) {
                manager.printMessage("wrong type (" + e.getMessage() + ")");
                return false;
            }
            manager.printMessage(stringValue);
        }
        return true;
    }

    public TableCommandGet(TableManager tableManager) {
        super("get", 2);
        manager = tableManager;
    }
}
