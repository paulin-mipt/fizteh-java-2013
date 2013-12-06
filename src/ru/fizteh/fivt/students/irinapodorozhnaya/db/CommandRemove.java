package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandRemove extends AbstractCommand {

    private final DbState state;
    public CommandRemove(DbState state) {
        super(1);
        this.state = state;
    }

    public String getName() {
        return "remove";
    }

    public void execute(String[] args) throws IOException {
        String s = state.removeValue(args[1]);
        if (s == null) {
            state.getOutputStream().println("not found");
        } else {
            state.getOutputStream().println("removed");
        }
    }
}
