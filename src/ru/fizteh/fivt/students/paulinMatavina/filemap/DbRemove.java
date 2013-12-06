package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbRemove implements Command {
    @Override
    public int execute(String[] args, State state) {
        String key = args[0];
        MyTableProvider multiState = (MyTableProvider) state;
        if (multiState.getCurrTable() == null) {
            System.out.println("no table");
            return 0;
        }
        
        if (multiState.getCurrTable().remove(key) != null) {
            if (multiState.currTableName.equals(key)) {
                multiState.currTableName = null;
            }
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }        
        return 0;
    }
    
    @Override
    public String getName() {
        return "remove";
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
