package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbUse implements Command {
    @Override
    public int execute(String[] args, State state) {
        String dbName = args[0];
        MyTableProvider multiState = (MyTableProvider) state;    
        Table table = null;
        try {
            table = multiState.tryToGetTable(dbName);
        } catch (Throwable e) {
            System.err.println("use: " + e.getMessage());
            return 1;
        }
        
        if (table == null) {
            System.out.println(dbName + " not exists");
        } else {
            if (multiState.getCurrTable() != null) {
                int chNum = ((MyTable) multiState.getCurrTable()).changesNum();
                if (chNum > 0) {
                    System.out.println(chNum + " unsaved changes");
                    return 0;
                }
            } 
            multiState.currTableName = dbName;
            System.out.println("using " + dbName);
        }
        return 0;
    }
    
    @Override
    public String getName() {
        return "use";
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
