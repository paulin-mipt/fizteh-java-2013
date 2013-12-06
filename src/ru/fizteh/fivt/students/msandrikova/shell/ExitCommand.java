package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.IOException;


public class ExitCommand extends Command {

    public ExitCommand() {
        super("exit", 0);
    }
    
    @Override
    public void execute(String[] argumentsList, Shell shell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
            return;
        }
        
        if (shell.getState().isMultiFileHashMap && shell.getState().currentTable != null) {
            shell.getState().currentTable.commit();
        }
        if (shell.getState().isStoreable && shell.getState().currentStoreableTable != null) {
            try {
                shell.getState().currentStoreableTable.commit();
            } catch (IOException e) {
                Utils.generateAnError(e.getMessage(), this.getName(), false);
            }
        }
        
        Thread.currentThread().interrupt();
    }
}
