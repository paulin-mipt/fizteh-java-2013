package ru.fizteh.fivt.students.krivchansky.shell;

public class ExitCommand implements Commands {
    
    public String getCommandName() {
        return "exit";
    }

    public int getArgumentQuantity() {
        return 0;
    }
    public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrongException {
        throw new SomethingIsWrongException("EXIT");
    }

}
