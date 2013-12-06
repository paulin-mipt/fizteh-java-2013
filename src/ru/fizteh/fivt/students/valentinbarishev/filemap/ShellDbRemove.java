package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

public final class ShellDbRemove extends SimpleShellCommand {
    private Context context;

    public ShellDbRemove(Context newContext) {
        context = newContext;
        setName("remove");
        setNumberOfArgs(2);
        setHint("usage: remove <key>");
    }

    @Override
    public void run() {
        if (context.table == null) {
            System.out.println("no table");
            return;
        }
        if (context.table.remove(getArg(1)) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
