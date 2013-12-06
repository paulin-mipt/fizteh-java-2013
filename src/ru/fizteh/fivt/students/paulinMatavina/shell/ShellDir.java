package ru.fizteh.fivt.students.paulinMatavina.shell; 

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class ShellDir implements Command {
    @Override
    public int execute(String[] args, State state) {
        if (!state.currentDir.exists()) {
            System.err.println("dir: not found " + state.currentDir);
            return 1;
        }
        if (!state.currentDir.isDirectory()) {
            System.err.println("dir: " + state.currentDir + " is not a directory");
            return 1;
        }
        String[] currentDirList = state.currentDir.list();
        for (int i = 0; i < currentDirList.length; i++) {
            System.out.println(currentDirList[i]);
        }
        return 0;
    }

    @Override
    public String getName() {
        return "dir";
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
