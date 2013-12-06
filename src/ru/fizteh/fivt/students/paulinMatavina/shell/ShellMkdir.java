package ru.fizteh.fivt.students.paulinMatavina.shell;

import java.io.File;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class ShellMkdir implements Command {
    @Override
    public int execute(String[] args, State state) {
        String name = args[0];
        if (name.equals("")) {
            throw new IllegalArgumentException("mkdir: no directory name entered");
        }

        File dir = new File(((ShellState) state).makeNewSource(name));
        if (dir.exists()) {
            if (dir.isDirectory()) {
                throw new IllegalArgumentException("mkdir: directory already exists");
            } else {
                throw new IllegalArgumentException("mkdir: directory can't be created");
            }
        }
        if (!dir.mkdirs()) {
            throw new IllegalArgumentException("mkdir: directory can't be created");
        }
        return 0;
    }

    @Override
    public String getName() {
        return "mkdir";
    }
    
    @Override
    public int getArgNum() {
        return 1;
    }
    
    @Override
    public boolean spaceAllowed() {
        return false;
    }
}
