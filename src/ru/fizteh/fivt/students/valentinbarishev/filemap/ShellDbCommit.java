package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

import java.io.IOException;

public class ShellDbCommit extends SimpleShellCommand {
    private Context context;

    public ShellDbCommit(final Context newContext) {
        context = newContext;
        setName("commit");
        setNumberOfArgs(1);
        setHint("usage: commit");
    }

    @Override
    public void run() {
        try {
            if (context.table != null) {
                System.out.println(context.table.commit());
            } else {
                System.out.println("no table");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
