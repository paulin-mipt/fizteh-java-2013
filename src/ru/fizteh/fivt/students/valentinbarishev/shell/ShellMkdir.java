package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellMkdir extends SimpleShellCommand {
    private Context context;

    public ShellMkdir(final Context newContext) {
        context = newContext;
        setName("mkdir");
        setNumberOfArgs(2);
        setHint("usage: mkdir <directory name>");
    }

    @Override
    public void run() {
        try {
            context.makeDir(getArg(1));
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " argument " + getArg(1));
        }
    }
}
