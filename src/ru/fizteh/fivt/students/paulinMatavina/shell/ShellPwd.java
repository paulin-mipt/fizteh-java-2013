package ru.fizteh.fivt.students.paulinMatavina.shell;

import java.io.IOException;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class ShellPwd implements Command {
    @Override
    public int execute(String[] args, State state) {
        try {
            System.out.println(state.currentDir.getCanonicalPath());
        } catch (IOException e) {
            System.err.println("pwd: internal error: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    @Override
    public String getName() {
        return "pwd";
    }
    
    @Override
    public int getArgNum() {
        return 0;
    }
    
    @Override
    public boolean spaceAllowed() {
        return false;
    }
}
