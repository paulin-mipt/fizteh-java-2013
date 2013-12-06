package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;


public class CommandDirectory extends AbstractCommand {
    
    private final StateShell state;
    
    CommandDirectory(StateShell state) {
        super(0);
        this.state = state;
    }
    
    public void execute(String[] args) {
        File[] filesList = state.getCurrentDir().listFiles();
        for (File s: filesList) {
            state.getOutputStream().println(s.getName());
        }
    }
    
    public String getName() {
        return "dir";
    }
}    
