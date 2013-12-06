package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;

public class ChangeDirectoryCommand extends Command {

    public ChangeDirectoryCommand() {
        super("cd", 1);
    }
    
    @Override
    public void execute(String[] argumentsList, Shell myShell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
            return;
        }
        
        File filePath = new File(argumentsList[1]);
        
        if (!filePath.isAbsolute()) {
            filePath = new File(myShell.getCurrentDirectory() + File.separator + argumentsList[1]);
        }
        if (!filePath.exists() || !filePath.isDirectory()) {
            Utils.generateAnError("\"" + argumentsList[1] + "\": No such directory.", this.getName(),
                    myShell.getIsInteractive());
            return;
        }
        myShell.setCurrentDirectory(filePath);
    }
}
