package ru.fizteh.fivt.students.baldindima.junit;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbGet extends ShellIsItCommand {
    private Context context;

    public ShellDbGet(Context nContext) {
        context = nContext;
        setName("get");
        setNumberOfArgs(2);

    }

    public void run() {
        if (context.table == null) {
            System.out.println("no table");
            return;
        }
        Storeable storeable = context.table.get(arguments[1]);
        if (storeable == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(context.provider.serialize(context.table, storeable));
        }
    }

}



