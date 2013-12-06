package ru.fizteh.fivt.students.lizaignatyeva.shell;

public abstract class Command {
    public String name;
    protected int argumentsAmount;
    public abstract void run(String[] args) throws Exception;
    protected boolean checkArguments(String[] args) throws Exception {
        return (args.length == argumentsAmount);
    }
}
