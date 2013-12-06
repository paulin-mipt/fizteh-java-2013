package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

public class ShellDbSize extends SimpleShellCommand {
    private Context context;

    public ShellDbSize(final Context newContext) {
        context = newContext;
        setName("size");
        setNumberOfArgs(1);
        setHint("usage: size");
    }

    @Override
    public void run() {
        if (context.table != null) {
            System.out.println(context.table.size());
        } else {
            System.out.println("no table");
        }
    }
}
