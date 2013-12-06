package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class CopyFileOrDirectoryCommand extends Command {

    public CopyFileOrDirectoryCommand() {
        super("cp", 2);
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
            Utils.generateAnError("Source and destination should be different", this.getName(),
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
                File newFile = new File(destination + File.separator + argumentsList[1]);
                try {
                    if (newFile.exists()) {
                        Utils.generateAnError("File or directory \"" + argumentsList[1] 
                                + "\" already exists in directory \"" 
                                + argumentsList[2] + "\"",
                                this.getName(), myShell.getIsInteractive());
                        return;
                    }
                    if (!Utils.copying(filePath, destination, this.getName(), 
                            myShell.getIsInteractive())) {
                        return;
                    }
                } catch (IOException e) {
                    Utils.generateAnError("Input or output error", this.getName(), false);
                }
                return;
            } else {
                Utils.generateAnError("Can not copy in existing file.", this.getName(),
                        myShell.getIsInteractive());
                return;
            }
        } else {
            boolean destinationIsDirectory = argumentsList[2].endsWith(File.separator);
            if (destinationIsDirectory) {
                Utils.generateAnError("Destination directory does not exist", this.getName(),
                        myShell.getIsInteractive());
                return;
            } else {
                if (destination.getParentFile().exists()) {
                    if (destinationIsDirectory == filePath.isDirectory()) {
                        if (destinationIsDirectory) {
                            try {
                                if (!Utils.copyDirectoriesInSameDirectory(filePath,
                                        destination,
                                        this.getName(), myShell.getIsInteractive())) {
                                    return;
                                } 
                            } catch (IOException e) {
                                Utils.generateAnError("Input or output error", this.getName(),
                                        false);
                            }  
                        } else {
                            try {
                                Utils.copyFiles(filePath, destination);
                            } catch (IOException e) {
                                Utils.generateAnError("Input or output error", this.getName(),
                                        false);
                            }
                            return;
                        }
                    } else {
                        Utils.generateAnError("Can not copy file and get directory or copy"
                                + " directory and get file.", this.getName()
                                , myShell.getIsInteractive());
                        return;
                    }
                } else {
                    Utils.generateAnError("Parent file for destination file does not exist", 
                            this.getName(), myShell.getIsInteractive());
                    return;
                }
            }
        }
    }
        
}
