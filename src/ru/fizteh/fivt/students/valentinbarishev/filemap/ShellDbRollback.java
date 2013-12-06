package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

public class ShellDbRollback extends SimpleShellCommand {
    private Context context;

    public ShellDbRollback(final Context newContext) {
        context = newContext;
        setName("rollback");
        setNumberOfArgs(1);
        setHint("usage: rollback");
    }

    @Override
    public void run() {
        if (context.table != null) {
            System.out.println(context.table.rollback());
        } else {
            System.out.println("no table");
        }
    }
}
