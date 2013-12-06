package ru.fizteh.fivt.students.baldindima.junit;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbCommit extends ShellIsItCommand {
    private Context context;

    public ShellDbCommit(Context nContext) {
        context = nContext;
        setName("commit");
        setNumberOfArgs(1);

    }

    public void run() throws IOException {
        if (context.table == null) {
            System.out.println("no table");
            return;
        } else {
            System.out.println(context.table.commit());
        }

    }

}



