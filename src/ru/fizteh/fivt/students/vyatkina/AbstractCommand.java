package ru.fizteh.fivt.students.vyatkina;

abstract public class AbstractCommand<State> implements Command {

    protected String name;
    protected int argsCount;
    public State state;

    public static String WRONG_NUMBER_OF_ARGUMENTS = "Wrong number of arguments";

    public AbstractCommand(State state) {
        this.state = state;
    }

    @Override
    abstract public void execute(String[] args) throws CommandExecutionException;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getArgumentCount() {
        return argsCount;
    }

    @Override
    public String[] parseArgs(String signature) throws IllegalArgumentException {
        if (signature.isEmpty() && getArgumentCount() == 0) {
            return new String[0];
        }
        String[] args = signature.split("\\s+");
        for (String arg : args) {
        }
        if (args.length != getArgumentCount()) {
            throw new IllegalArgumentException(WRONG_NUMBER_OF_ARGUMENTS);
        }
        return args;
    }
}
