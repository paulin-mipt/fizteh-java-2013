package ru.fizteh.fivt.students.kislenko.shell;

public interface Command<State> {
    String getName();

    int getArgCount();

    void run(State state, String[] args) throws Exception;
}
