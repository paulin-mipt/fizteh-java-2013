package ru.fizteh.fivt.students.dmitryKonturov.shell;

public interface ShellCommand {
    String getName();

    void execute(String[] args, ShellInfo info) throws ShellException;
}
