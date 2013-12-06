package ru.fizteh.fivt.students.baldindima.junit;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbRollback extends ShellIsItCommand {
    private Context context;

    public ShellDbRollback(Context nContext) {
        context = nContext;
        setName("rollback");
        setNumberOfArgs(1);

    }

    public void run() throws IOException {
        if (context.table == null) {
            System.out.println("no table");
            return;
        } else {
            System.out.println(context.table.rollback());
        }

    }

}



