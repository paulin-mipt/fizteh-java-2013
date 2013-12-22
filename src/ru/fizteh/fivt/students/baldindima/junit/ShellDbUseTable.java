package ru.fizteh.fivt.students.baldindima.junit;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbUseTable extends ShellIsItCommand {
    private Context context;

    public ShellDbUseTable(Context nContext) {
        context = nContext;
        setName("use");
        setNumberOfArgs(2);

    }

    public void run() throws IOException {
    	 if ((context.table != null) && (context.getChanges() != 0)) {
             System.out.println(context.getChanges() + " unsaved changes");
             return;
         }
    	 Table oldTable = context.table;
         context.table = context.provider.getTable(arguments[1]);
         if (context.table != null) {
             System.out.println("using " + arguments[1]);
         } else {
             System.out.println(arguments[1] + " not exists");
             context.table = oldTable;
         }
    }
    
}

