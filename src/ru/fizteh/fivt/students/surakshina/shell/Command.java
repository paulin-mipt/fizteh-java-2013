package ru.fizteh.fivt.students.surakshina.shell;

public interface Command {
    String getName();

    int numberOfArguments();

    void executeProcess(String[] input);
}
