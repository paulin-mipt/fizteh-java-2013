package ru.fizteh.fivt.students.krivchansky.shell;

import java.io.File;
import java.io.IOException;


public class ChangeDirectoryCommand implements Commands{
    
    public String getCommandName() {
        return "cd";
    }

    public int getArgumentQuantity() {
        return 1;
    }
    
    public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrongException {
        String newLocation = args[0]; //path
        File newDisposition = UtilMethods.getAbsoluteName(newLocation, state);//Directory
        if (!newDisposition.isDirectory()) {
            throw new SomethingIsWrongException("cd: " + newLocation + "such directory doesn't exist.");
        }
        try {
            state.changeCurDir(newDisposition.getCanonicalPath());
        } catch (IOException e) {
            throw new SomethingIsWrongException(
                    "cd: Error aquired while getting canonical path of new directory message: "
                    + e.getMessage());
        }
    }
}
