package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MultiDbState;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandRollBack extends AbstractCommand {
    private final MultiDbState state;
    
    public CommandRollBack(MultiDbState state) {
        super(0);
        this.state = state;
    }
    
    public String getName() {
        return "rollback";
    }
    
    public void execute(String[] args) throws IOException {
        System.out.println(state.rollBack());
    } 
}

