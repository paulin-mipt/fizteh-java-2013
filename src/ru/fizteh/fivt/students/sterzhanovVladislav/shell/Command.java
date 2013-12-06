package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

import java.io.IOException;

public abstract class Command {
    protected Shell parentShell;
    public abstract CommandParser getParser();
    public abstract void innerExecute(String[] args) throws Exception, IOException;
    
    private int argc;
    
    void execute(String... args) throws IllegalArgumentException, Exception {
        if (args == null || (argc != -1 && args.length != argc))  {
            throw new IllegalArgumentException("Wrong number of arguments given");
        }
        if (parentShell == null) {
            throw new Exception("Cannot execute a command without a shell attached");
        }
        innerExecute(args);
    }
    
    public Command setShell(Shell newShell) {
        parentShell = newShell;
        return this;
    }
    
 
    public Command(int argCount) {
        argc = argCount;
    }
}
