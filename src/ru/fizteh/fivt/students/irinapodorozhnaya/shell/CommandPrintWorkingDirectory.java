package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;


public class CommandPrintWorkingDirectory extends AbstractCommand {
    
    private final StateShell state;
    
    public CommandPrintWorkingDirectory(StateShell st) {
        super(0);
        state = st;
    }
    
    public String getName() {
        return "pwd";
    }
    
    public void execute(String[] args) throws IOException {
        state.getOutputStream().println(state.getCurrentDir().getCanonicalPath());
    }
}
