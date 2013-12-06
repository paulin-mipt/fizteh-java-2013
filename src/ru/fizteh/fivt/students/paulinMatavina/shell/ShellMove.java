package ru.fizteh.fivt.students.paulinMatavina.shell;

import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class ShellMove implements Command {
    @Override
    public int execute(String[] args, State state) {
        
        String newSourceStr = ((ShellState) state).makeNewSource(args[0]);
        String newDestStr = ((ShellState) state).makeNewSource(args[1]);
        File source = new File(newSourceStr);
        File dest = new File(newDestStr);
        if (dest.isDirectory()) {
            dest = new File(newDestStr + File.separator + source.getName());
        }
        try {
            if (dest.getCanonicalPath().equals(source.getCanonicalPath())) {
                System.err.println("move: source path equals destination path");
                return 1;
            }
        } catch (IOException e) {
            System.err.println("copy: internal error: " + e.getMessage());
        }
        int check = ShellUtils.moveCopyCheck(source, dest);
        if (check != 0) {
            return check;
        }
        try {
            Files.move(source.toPath(), dest.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("mv: error: " + e.getMessage());
            return 1;
        }
        return 0;
    }
    
    @Override
    public String getName() {
        return "mv";
    }
    
    @Override
    public int getArgNum() {
        return 2;
    }
    
    @Override
    public boolean spaceAllowed() {
        return false;
    }
}
