package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class RemoveFileOrDirectoryCommand extends Command {

    public RemoveFileOrDirectoryCommand() {
        super("rm", 1);
    }
    
    @Override
    public void execute(String[] argumentsList, Shell myShell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
            return;
        }
        
        File filePath = new File(myShell.getCurrentDirectory() + File.separator + argumentsList[1]);
        
        try {
            if (!Utils.remover(filePath, this.getName(), myShell.getIsInteractive())) {
                return;
            }
        } catch (IOException e) {
            Utils.generateAnError("Input or output error", this.getName(), false);
        }
    }

}
