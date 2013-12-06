package ru.fizteh.fivt.students.dobrinevski.shell;


public abstract class Command {
    public Shell parentShell;
    public Integer argc;
    public String[] returnValue = null;
    public Command(int argCount) {
        argc = argCount;
    }
    public abstract void innerExecute(String[] args) throws Exception;
    void execute(String[] args) throws Exception {
        if (args == null || args.length != argc)  {
            throw new Exception("Wrong number of arguments");
        }
        innerExecute(args);
    }
}
