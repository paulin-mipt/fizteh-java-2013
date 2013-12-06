package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class DropCommand extends AbstractCommand {

    public DropCommand(FilesystemState s) {
        super(s, "drop", 1);
    }

    public void execute(String[] args) throws IOException {
        String name = args[1];
        FilesystemState table = getState();
        if (table.provider.getTable(name) != null) {
            table.provider.removeTable(name);
            if (table.getWorkingDirectory() != null && table.getWorkingDirectory().getName().equals(name)) {
                table.setWorkingDirectory(null);
            }
            getState().getStream().println("dropped");
        } else {
            getState().getStream().println(name + " not exists");
        }
    }
}
