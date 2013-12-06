package ru.fizteh.fivt.students.ichalovaDiana.shell;

public abstract class Command {
    public boolean rawArgumentsNeeded;
    
    protected abstract void execute(String... arguments) throws Exception;
}
