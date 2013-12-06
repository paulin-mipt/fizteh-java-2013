package ru.fizteh.fivt.students.krivchansky.shell;

import java.io.File;


public class MakeDirectoryCommand implements Commands {
    
    public String getCommandName() {
        return "mkdir";
    }

    public int getArgumentQuantity() {
        return 1;
    }
    
    public void implement (String[] args, Shell.ShellState state) throws SomethingIsWrongException{
        String nameOfDirectory = args [0];
        File creating = UtilMethods.getAbsoluteName(nameOfDirectory, state);
        if (!creating.mkdir()) {
            throw new SomethingIsWrongException("Can't make a directory " + nameOfDirectory);
        }
    }
}