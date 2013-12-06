package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbCommit implements Command {
    @Override
    public int execute(String[] args, State state) {
        MyTableProvider multiState = (MyTableProvider) state;
        if (multiState.getCurrTable() == null) {
            System.out.println("no table");
            return 0;
        }
        
        int result = 0;
        try {
            result = multiState.getCurrTable().commit();
        } catch (Exception e) {
            System.err.println("commit: " + e.getMessage());
            return 1;
        } 
        System.out.println(result);
        return 0;
    }
    
    @Override
    public String getName() {
        return "commit";
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
