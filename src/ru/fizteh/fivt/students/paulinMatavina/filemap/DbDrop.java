package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbDrop implements Command {
    @Override
    public int execute(String[] args, State state) {
        String name = args[0];
        
        MyTableProvider multiState = (MyTableProvider) state;
        try {
            multiState.removeTable(name);
        } catch (IllegalStateException e) {
            System.out.println(name + " not exists");
            return 0;
        }
      
        System.out.println("dropped");
        return 0;
    }
    
    @Override
    public String getName() {
        return "drop";
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
