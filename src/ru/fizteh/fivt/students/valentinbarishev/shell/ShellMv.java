package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellMv extends SimpleShellCommand {
    private Context context;

    public ShellMv(final Context newContext) {
        context = newContext;
        setName("mv");
        setNumberOfArgs(3);
        setHint("usage: mv <source path> <destination path>");
    }

    @Override
    public void run() {
        try {
            context.move(getArg(1), getArg(2));
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " bad arguments " + getArg(1) + " " + getArg(2));
        }
    }
}
