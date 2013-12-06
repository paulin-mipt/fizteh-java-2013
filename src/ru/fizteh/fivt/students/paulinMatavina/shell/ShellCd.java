package ru.fizteh.fivt.students.paulinMatavina.shell;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class ShellCd implements Command {
    @Override
    public int execute(String[] args, State state) {
        return ((ShellState) state).cd(args[0]);
    }
    
    @Override
    public String getName() {
        return "cd";
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
