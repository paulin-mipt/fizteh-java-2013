package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.util.ArrayList;
import java.util.StringTokenizer;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;
import ru.fizteh.fivt.storage.structured.Table;

public class DbCreate implements Command {
    @Override
    public int execute(String[] args, State state) {
        String arg = args[0].trim();
        if (arg.charAt(arg.length() - 1) != ')') {
            System.out.println("wrong type (usage: create <name> (<type1 [type2 ...]>))");
            return 0; 
        }
        arg = arg.substring(0, arg.length() - 1).trim();
        String[] argArray = arg.split("[/(]", 2);
        if (argArray.length < 2 || argArray[1].isEmpty()) {
            System.out.println("wrong type (usage: create <name> (<type1 [type2 ...]>))");
            return 0;
        }
        
        StringTokenizer tokens = new StringTokenizer(argArray[1]);
        String name = argArray[0].trim();
        MyTableProvider multiState = (MyTableProvider) state;
        Table table = null;

        try {
            ArrayList<Class<?>> signature = multiState.parseSignature(tokens);
            table = multiState.createTable(name, signature);
        } catch (DbWrongTypeException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
            return 0;
        } catch (Exception e) {
            System.err.println("create: " + e.getMessage());
            return 1;
        }
        
        if (table == null) {
            System.out.println(name + " exists");
        }  else {
            System.out.println("created");
        }
        return 0;
    }
    
    @Override
    public String getName() {
        return "create";
    }
    
    @Override
    public int getArgNum() {
        return 1;
    }   
    
    @Override
    public boolean spaceAllowed() {
        return true;
    }
}
