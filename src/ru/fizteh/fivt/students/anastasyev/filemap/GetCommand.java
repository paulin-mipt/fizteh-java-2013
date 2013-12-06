package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class GetCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider provider, String[] command) {
        if (command.length != 2) {
            System.err.println("get: Usage - get key");
            return false;
        }
        try {
            FileMapTable currTable = provider.getCurrentFileMapTable();
            if (currTable == null) {
                System.out.println("no table");
                return false;
            }
            Storeable str = currTable.get(command[1]);
            if (str == null) {
                System.out.println("not found");

            } else {
                System.out.println("found");
                System.out.println(provider.serialize(currTable, str));
            }
        } catch (Exception e) {
            System.err.println("get: Can't get the key");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "get";
    }
}
