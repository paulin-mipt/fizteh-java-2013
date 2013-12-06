package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.anastasyev.shell.Command;

import java.text.ParseException;

public class PutCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider provider, String[] command) {
        if (command.length < 3) {
            System.err.println("wrong type (Usage - put key value)");
            return false;
        }
        String arg1 = command[1];
        String arg2 = command[2];
        FileMapTable currTable = provider.getCurrentFileMapTable();
        if (currTable == null) {
            System.out.println("no table");
            return false;
        }
        Storeable result = null;
        try {
            result = currTable.put(arg1.trim(), provider.deserialize(currTable, arg2.trim()));
        } catch (ParseException e) {
            System.err.println("wrong type (" + e.getMessage() + ")");
            return false;
        }
        if (result == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println("old " + provider.serialize(currTable, result));
        }
        return true;
    }

    @Override
    public String commandName() {
        return "put";
    }
}
