package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.IOException;


public class CommandMove extends AbstractCommand {
    
    private final StateShell state;
    public CommandMove(StateShell st) {
        super(2);
        state = st;
    }
    
    public String getName() {
        return "mv";
    }
    
    public void execute(String[] args) throws IOException {    
        File source = state.getFileByName(args[1]);
        File dest = state.getFileByName(args[2]);
        if (!source.exists()) {
            throw new IOException("mv: '" + args[1] + "' not exist");
        } else if (dest.isDirectory()) {
            if (!source.renameTo(new File(dest + File.separator + source.getName()))) {
                throw new IOException("mv: '" + source.getName() + "' can't move file");
            }
        } else if (!source.renameTo(dest)) {
            throw new IOException("mv: '" + source.getName() + "' can't move file");
        }        
    }
}
