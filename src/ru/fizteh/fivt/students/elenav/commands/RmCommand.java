package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.elenav.shell.ShellState;

public class RmCommand extends AbstractCommand {
    public RmCommand(ShellState s) { 
        super(s, "rm", 1);
    }
    
    private void deleteRecursively(String path) throws IOException {
        File f = new File(path);
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteRecursively(file.getAbsolutePath());
                }
            }
        }
        if (!f.delete()) {
            throw new IOException("rm: cannot remove '" + f.getName() + "': Unknown error");
        }
    }
    
    public void execute(String[] args) throws IOException {
        File f = new File(absolutePath(args[1]));
        if (!f.exists()) {
            throw new IOException("rm: cannot remove '" + args[1] + "': No such file or directory");
        } else {
            deleteRecursively(f.getAbsolutePath());
        }
    }

}

