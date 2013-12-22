package ru.fizteh.fivt.students.dubovpavel.executor;

public abstract class Performer<D extends Dispatcher> {
    public abstract boolean pertains(Command command);

    public abstract void execute(D dispatcher, Command command) throws PerformerException;
}
