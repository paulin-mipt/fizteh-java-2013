package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class FileMapExitCommand implements Command<FileMapTableProvider> {
    @Override
    public final boolean exec(FileMapTableProvider state, final String[] command) {
        if (command.length != 1) {
            System.err.println("exit: Usage - exit");
            return false;
        }
        System.exit(0);
        return true;
    }

    @Override
    public final String commandName() {
        return "exit";
    }
}
