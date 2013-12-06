package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MultiDbState;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandSize extends AbstractCommand {
    private final MultiDbState state;
    
    public CommandSize(MultiDbState state) {
        super(0);
        this.state = state;
    }
    
    public String getName() {
        return "size";
    }
    
    public void execute(String[] args) throws IOException {
        state.getOutputStream().println(state.getCurrentTableSize());
    } 
}
