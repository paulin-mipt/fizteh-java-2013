package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandPut extends AbstractCommand {
    
    private final DbState state;
    public CommandPut(DbState state) {
        super(2);
        this.state = state;
    }

    public String getName() {
        return "put";
    }

    public void execute(String[] args) throws IOException {
        String s = state.put(args[1], args[2]);
        if (s != null) {
            state.getOutputStream().println("overwrite\n" + s);
        } else {
            state.getOutputStream().println("new");
        }
    }
}
