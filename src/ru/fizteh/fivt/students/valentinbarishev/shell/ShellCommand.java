package ru.fizteh.fivt.students.valentinbarishev.shell;

public interface ShellCommand {
    void run();
    boolean isMyCommand(CommandString command);
}
