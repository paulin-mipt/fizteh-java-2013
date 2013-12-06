package ru.fizteh.fivt.students.baldindima.junit;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbDropTable extends ShellIsItCommand {
    private Context context;

    public ShellDbDropTable(Context nContext) {
        context = nContext;
        setName("drop");
        setNumberOfArgs(2);

    }

    public void run() throws IOException {
    	 try {
             if ((context.table != null) && (context.table.getName().equals(arguments[1]))) {
                 context.table = null;
             }

             context.provider.removeTable(arguments[1]);
             System.out.println("dropped");
         } catch (IllegalStateException e) {
             System.out.println(arguments[1] + " not exists");
         }
     
    }

}

