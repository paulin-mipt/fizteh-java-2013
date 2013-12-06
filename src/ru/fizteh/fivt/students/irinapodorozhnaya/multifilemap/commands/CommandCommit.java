package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands;

import java.io.IOException;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MultiDbState;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandCommit extends AbstractCommand {
    private final MultiDbState state;
    
    public CommandCommit(MultiDbState state) {
        super(0);
        this.state = state;
    }
    
    public String getName() {
        return "commit";
    }
    
    public void execute(String[] args) throws IOException {
        state.getOutputStream().println(state.commitDif());
    } 
}
