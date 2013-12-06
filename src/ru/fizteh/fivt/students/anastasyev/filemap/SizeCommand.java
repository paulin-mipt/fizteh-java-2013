package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class SizeCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider state, String[] command) {
        if (command.length != 1) {
            System.err.println("size: Usage - size");
            return false;
        }
        FileMapTable fileMapTable = state.getCurrentFileMapTable();
        if (fileMapTable == null) {
            System.out.println("no table");
            return false;
        }
        System.out.println(fileMapTable.size());
        return true;
    }

    @Override
    public String commandName() {
        return "size";
    }
}
