package ru.fizteh.fivt.students.paulinMatavina.shell;

import java.io.File;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class ShellMkdir implements Command {
    @Override
    public int execute(String[] args, State state) {
        String name = args[0];
        if (name.equals("")) {
            System.err.println("mkdir: no directory name entered");
            throw new IllegalArgumentException();
        }

        File dir = new File(((ShellState) state).makeNewSource(name));
        if (dir.exists()) {
            if (dir.isDirectory()) {
                //System.err.println("mkdir: directory already exists");
                return 2;
            } else {
                System.err.println("mkdir: directory can't be created");
                throw new IllegalArgumentException();
            }
        }
        if (!dir.mkdir()) {
            System.err.println("mkdir: directory can't be created");
            throw new IllegalArgumentException();
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
