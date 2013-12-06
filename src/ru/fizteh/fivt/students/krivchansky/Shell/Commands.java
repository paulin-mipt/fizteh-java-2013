package ru.fizteh.fivt.students.krivchansky.shell;



public interface Commands {

    public String getCommandName();
    
    public int getArgumentQuantity();
    
    abstract public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrongException;
}
