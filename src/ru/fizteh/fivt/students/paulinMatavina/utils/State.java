package ru.fizteh.fivt.students.paulinMatavina.utils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

public abstract class State {
    public File currentDir;
    public HashMap<String, Command> commands;
    public InputStream in;
    
    public State() {
        commands = new HashMap<String, Command>();
        currentDir = new File(".");
        in = System.in;
    }
    
    public String makeNewSource(final String source) {
        File newFile = new File(source);
        if (newFile.isAbsolute()) {
            return newFile.getAbsolutePath();
        } else {
            return currentDir.getAbsolutePath() + File.separator + source;
        }
    }

    public String makeNewSource(final String source, final String source2) {
        File newFile = new File(source);
        if (newFile.isAbsolute()) {
            return newFile.getAbsolutePath() + File.separator + source2;
        } else {
            return currentDir.getAbsolutePath() + File.separator + source 
                                        + File.separator + source2;
        }
    }
    
    public void add(Command command) {
        commands.put(command.getName(), command);
    } 
    
    public int exitWithError(int errCode) throws ExitException {
        return errCode;
    }
}
