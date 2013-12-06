package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class RollbackCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider state, String[] command) {
        if (command.length != 1) {
            System.err.println("rollback: Usage - rollback");
            return false;
        }
        FileMapTable fileMapTable = state.getCurrentFileMapTable();
        if (fileMapTable == null) {
            System.out.println("no table");
            return false;
        }
        try {
            System.out.println(fileMapTable.rollback());
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
        return true;
    }

    @Override
    public String commandName() {
        return "rollback";
    }
}
