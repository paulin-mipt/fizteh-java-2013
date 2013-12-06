package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class MoveFileOrDirectoryCommand extends Command {

    public MoveFileOrDirectoryCommand() {
        super("mv", 2);
    }
    
    @Override
    public void execute(String[] argumentsList, Shell myShell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
            return;
        }

        File destination = new File(argumentsList[2]);
        if (!destination.isAbsolute()) {
            destination = new File(myShell.getCurrentDirectory() + File.separator + destination);
        }
        destination = destination.getAbsoluteFile();
        File filePath = new File(argumentsList[1]);
        if (!filePath.isAbsolute()) {
            filePath = new File(myShell.getCurrentDirectory() + File.separator + filePath);
        }
        filePath = filePath.getAbsoluteFile();
        
        if (filePath.equals(destination)) {
            Utils.generateAnError("Source and destination should be different",
                    this.getName(),
                    myShell.getIsInteractive());
            return;
        }
        if (!filePath.exists()) {
            Utils.generateAnError("\"" + argumentsList[1] + "\": No such file or directory",
                    this.getName(), myShell.getIsInteractive());
            return;
        }
        
        if (destination.exists()) {
            if (destination.isDirectory()) {
                try {
                    if (!Utils.copying(filePath, destination, this.getName(),
                            myShell.getIsInteractive())) {
                        return;
                    }
                    if (!Utils.remover(filePath, this.getName(), myShell.getIsInteractive())) {
                        return;
                    }
                } catch (IOException e) {
                    Utils.generateAnError("Input or output error", this.getName(), false);
                }
            } else {
                Utils.generateAnError("Can not move in existing file: \"" 
            + argumentsList[2] + "\"", this.getName(), myShell.getIsInteractive());
                return;
            }
            
        } else {
            try {
                File parentFile = filePath.getCanonicalFile().getParentFile();
                if (destination.getCanonicalFile().getParentFile().equals(parentFile)) {
                    boolean destinationIsDirectory = argumentsList[2].endsWith(File.separator);
                    if (destinationIsDirectory == filePath.isDirectory()) {
                        filePath.renameTo(destination);
                    } else {
                        Utils.generateAnError("Can not rename file and get directory or rename "
                                + "directory and get file.", this.getName(), 
                                myShell.getIsInteractive());
                        return;
                    }
                } else {
                    Utils.generateAnError("Destination does not exist and does not locate in "
                            + "the same directory with source.", this.getName(), 
                            myShell.getIsInteractive());
                    return;
                }
            } catch (IOException e) {
                Utils.generateAnError("Input or output error", this.getName(), false);
            }
        }
    }

}
