package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellCd extends SimpleShellCommand {
    private Context context;

    public ShellCd(final Context newContext) {
        context = newContext;
        setName("cd");
        setNumberOfArgs(2);
        setHint("usage: cd <path>");
    }

    @Override
    public void run() {
        try {
            context.changeDir(getArg(1));
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " argument: " + getArg(1) + " " + e.getMessage());
        }
    }
}
