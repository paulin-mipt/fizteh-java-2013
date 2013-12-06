package ru.fizteh.fivt.students.vishnevskiy.shell;

import ru.fizteh.fivt.students.vishnevskiy.filemap.SingleFileMap;

public abstract class Command {
    public abstract String getName();

    public abstract int getArgsNum();

    public abstract void execute(State state, String[] args) throws CommandException;
}
