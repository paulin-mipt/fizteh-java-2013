package ru.fizteh.fivt.students.anastasyev.shell;


public final class Main {
    private Main() { }
    public static void main(final String[] args) {
        Shell shell = new Shell();
        Launcher launcher = new Launcher(shell, args);
    }
}
