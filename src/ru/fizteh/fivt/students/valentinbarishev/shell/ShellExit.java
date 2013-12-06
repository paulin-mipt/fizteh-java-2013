package ru.fizteh.fivt.students.valentinbarishev.shell;

public final class ShellExit extends SimpleShellCommand {

    public ShellExit() {
        setName("exit");
        setNumberOfArgs(1);
        setHint("usage: exit");
    }

    @Override
    public void run() {
        System.exit(0);
    }

}
