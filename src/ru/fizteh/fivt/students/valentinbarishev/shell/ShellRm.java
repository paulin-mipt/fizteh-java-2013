package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellRm extends SimpleShellCommand {
    private Context context;

    public ShellRm(final Context newContext) {
        context = newContext;
        setName("rm");
        setNumberOfArgs(2);
        setHint("usage: rm <something>");
    }

    @Override
    public void run() {
        try {
            context.remove(getArg(1));
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " argument " + getArg(1) + " " + e.getMessage());
        }
    }
}
