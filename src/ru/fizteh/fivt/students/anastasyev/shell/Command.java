package ru.fizteh.fivt.students.anastasyev.shell;

public interface Command<State> {
    boolean exec(State state, String[] command);

    String commandName();
}
