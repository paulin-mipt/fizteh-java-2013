package ru.fizteh.fivt.students.krivchansky.shell;

import java.io.File;


public class RemoveCommand implements Commands {
    
    public String getCommandName() {
        return "rm";
    }

    public int getArgumentQuantity() {
        return 1;
    }
    
    public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrongException {
        String whoToDelete = args[0];
        File deleteIt = UtilMethods.getAbsoluteName(whoToDelete, state);
        try {
            if (deleteIt.exists()) {
                if(!deleteIt.delete()) {
                    throw new SomethingIsWrongException("Error acquired while deleting a file");
                }
            } else {
                throw new SomethingIsWrongException ("The file, you want to delete, doesn't exist.");
            }
        } catch (SecurityException e) {
            throw new SomethingIsWrongException("You don't have enogh rights to delete this file. " + e.getMessage());
        }
    }

}
