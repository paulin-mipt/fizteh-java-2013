package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

public class ShellDbGet extends SimpleShellCommand {
    private Context context;

    public ShellDbGet(final Context newContext) {
        context = newContext;
        setName("get");
        setNumberOfArgs(2);
        setHint("usage: get <key>");
    }

    @Override
    public void run() {
        if (context.table == null) {
            System.out.println("no table");
            return;
        }
        Storeable storeable = context.table.get(getArg(1));
        if (storeable == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(context.provider.serialize(context.table, storeable));
        }
    }
}
