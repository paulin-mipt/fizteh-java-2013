package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.IOException;


public class CommandRemove extends AbstractCommand {
    
    private final StateShell state;
    
    CommandRemove(StateShell st) {
        super(1);
        state = st;
    }
    
    public String getName() {
        return "rm";
    }
    
    public void execute(String[] args) throws IOException {
        File f = state.getFileByName(args[1]);
        if (f.exists()) {
            if (f.getCanonicalPath().equals(state.getCurrentDir().getCanonicalPath())) {
                throw new IOException("rm: '" + args[1] + "' can't delete current directory");
            }
            deleteRecursivly(f);
        } else {
            throw new IOException("rm: '" + args[1] + "doesn't exist");
        }
    }
    
    public static void deleteRecursivly(File f) {
        if (f.isDirectory()) {
            for (File s: f.listFiles()) {
                deleteRecursivly(s);
            }
        }
        if (!f.delete()) {
            throw new IllegalStateException(f.getName() + ": can't delete file or directory");
        }
    }
}
