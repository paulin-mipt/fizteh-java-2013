package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;

public class MakeDirectoryCommand extends Command {

    public MakeDirectoryCommand() {
        super("mkdir", 1);
    }

    @Override
    public void execute(String[] argumentsList, Shell myShell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
            return;
        }
        
        File fileName = new File(myShell.getCurrentDirectory() + File.separator + argumentsList[1]);
        if (fileName.exists()) {
            Utils.generateAnError("Directory with name \"" + argumentsList[1] + "\" already exists",
                    this.getName(), myShell.getIsInteractive());
            return;
        }
        try {
            if (!fileName.mkdirs()) {
                Utils.generateAnError("Directory with name \"" + argumentsList[1] 
                        + "\" can not be created", this.getName(), myShell.getIsInteractive());
                return;
            }
        } catch (SecurityException e) {
            Utils.generateAnError("Directory with name \"" + argumentsList[1] 
                    + "\" can not be created", this.getName(), myShell.getIsInteractive());
            return;
        }
    }
}
