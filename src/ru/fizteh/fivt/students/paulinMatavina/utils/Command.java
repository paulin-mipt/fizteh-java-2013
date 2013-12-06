package ru.fizteh.fivt.students.paulinMatavina.utils;

public interface Command {
    int execute(String[] args, State state) throws IllegalArgumentException;

    String getName();
    int getArgNum();
    boolean spaceAllowed();
}
