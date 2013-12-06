package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.text.ParseException;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbPut implements Command {
    @Override
    public int execute(String[] args, State state) {
        String key = args[0];
        MyTableProvider multiState = (MyTableProvider) state;
        if (multiState.getCurrTable() == null) {
            System.out.println("no table");
            return 0;
        }
        Storeable value;
        try {
            value = multiState.deserialize(multiState.getCurrTable(), args[1]);
        } catch (ParseException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
            return 0;
        }
        Storeable result = multiState.getCurrTable().put(key, value);  
        if (result == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(multiState.serialize(multiState.getCurrTable(), result));
        }
        return 0;
    }
    
    @Override
    public boolean spaceAllowed() {
        return true;
    }
    @Override
    public String getName() {
        return "put";
    }
    
    @Override
    public int getArgNum() {
        return 2;
    }
}
