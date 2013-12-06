package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class DropCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider state, String[] command) {
        if (command.length != 2) {
            System.err.println("drop: Usage - drop tablename");
            return false;
        }
        try {
            state.removeTable(command[1]);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (IllegalStateException e) {
            System.out.println(command[1] + " not exists");
            return false;
        }
        System.out.println("dropped");
        return true;
    }

    @Override
    public String commandName() {
        return "drop";
    }
}
