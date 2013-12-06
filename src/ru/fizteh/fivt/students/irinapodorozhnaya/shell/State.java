package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public abstract class State implements StateInterface {
    private final InputStream in;
    private final PrintStream out;
    private final Map<String, Command> commands;
    private File currentDir;    
    
    public State(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
        commands = new HashMap<String, Command>();
    }
    
    public void add(Command com) {
        commands.put(com.getName(), com);
    }

    public InputStream getInputStream() {
        return in;
    }

    public PrintStream getOutputStream() {
        return out;
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(File currentDir) throws IOException {
        this.currentDir = currentDir;
    }

    public void checkAndExecute(String[] args) throws IOException {
        Command c = commands.get(args[0]);
         if (c != null) {
             int argsNumber = c.getNumberOfArguments();
             if (argsNumber >= 0) {
                     if (argsNumber > args.length - 1) {
                         throw new IOException(args[0] + ": Too few arguments");
                     } else if (argsNumber < args.length - 1) {
                         throw new IOException(args[0] + ": Too many arguments");
                     }
             }
             c.execute(args);
         } else {
             throw new IOException(args[0] + ": No such command");
         }        
    }

    public int commitDif() throws IOException {
        return 0;
    }
}
