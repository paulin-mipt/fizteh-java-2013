package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

import java.io.IOException;

public class UseCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider state, String[] command) {
        if (command.length != 2) {
            System.err.println("use: Usage - use tablename");
            return false;
        }
        try {
            state.setCurrentTable(command[1]);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "use";
    }
}
