package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands;

import java.io.IOException;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MultiDbState;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandCreate extends AbstractCommand{
    private MultiDbState state;
    
    public CommandCreate(MultiDbState st) {
        super(1);
        state = st;
    }
    
    public String getName() {
        return "create";
    }
    
    public void execute(String[] args) throws IOException {
        try {
            state.create(args[1], null);
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }
        state.getOutputStream().println("created");
    }
}
