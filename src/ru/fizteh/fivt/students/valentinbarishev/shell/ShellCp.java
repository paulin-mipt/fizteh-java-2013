package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellCp extends SimpleShellCommand {
    private Context context;

    public ShellCp(final Context newContext) {
        context = newContext;
        setName("cp");
        setNumberOfArgs(3);
        setHint("usage: cp <source path> <destination path>");
    }

    @Override
    public void run() {
        try {
            context.copy(getArg(1), getArg(2));
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " " + e.getMessage());
        }
    }
}
