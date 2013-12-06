package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.IOException;

public class PrintWorkingDirectoryCommand extends Command {
    
    public PrintWorkingDirectoryCommand() {
        super("pwd", 0);
    }

    @Override
    public void execute(String[] argumentsList, Shell myShell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, myShell.getIsInteractive())) {
            return;
        }

        try {
            String filePath = myShell.getCurrentDirectory().getCanonicalPath();
            System.out.println(filePath);
        } catch (IOException e) {
            Utils.generateAnError("Input or output error", this.getName(), false);
        }
        return;
    }
    
}
