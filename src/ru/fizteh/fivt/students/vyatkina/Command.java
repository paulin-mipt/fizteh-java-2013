package ru.fizteh.fivt.students.vyatkina;

public interface Command {

    void execute(String[] args) throws CommandExecutionException;

    String getName();

    int getArgumentCount();

    String[] parseArgs(String line);

}
