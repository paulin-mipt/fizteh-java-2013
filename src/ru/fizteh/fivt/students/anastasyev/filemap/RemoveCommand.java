package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class RemoveCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider state, String[] command) {
        if (command.length != 2) {
            System.err.println("remove: Usage - remove key");
            return false;
        }
        try {
            FileMapTable currTable = state.getCurrentFileMapTable();
            if (currTable == null) {
                System.out.println("no table");
                return false;
            }
            Storeable str = currTable.remove(command[1]);
            if (str == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } catch (Exception e) {
            System.err.println("remove: Can't remove the key");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "remove";
    }
}
