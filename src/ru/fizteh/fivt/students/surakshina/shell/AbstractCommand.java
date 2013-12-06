package ru.fizteh.fivt.students.surakshina.shell;

public abstract class AbstractCommand implements Command {
    protected int numberOfArguments;
    protected String name;
    protected State state;

    public AbstractCommand(State stateNew) {
        this.state = stateNew;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int numberOfArguments() {
        return numberOfArguments;
    }

    @Override
    public abstract void executeProcess(String[] input);
}
