package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class UseCommand extends Command {

    public UseCommand() {
        super("use", 1);
    }

    @Override
    public void execute(String[] argumentsList, Shell shell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
            return;
        }
        
        
        String name = argumentsList[1];
        Object currentTable = null;
        
        if (!shell.getState().isStoreable) {
            try {
                currentTable = shell.getState().tableProvider.getTable(name);
            } catch (IllegalArgumentException e) {
                System.out.println("wrong type (" + e.getMessage() + ")");
                return;
            }
        } else {
            try {
                currentTable = shell.getState().storeableTableProvider.getTable(name);
            } catch (IllegalArgumentException e) {
                System.out.println("wrong type (" + e.getMessage() + ")");
                return;
            }
        }
        
        
        
        if (currentTable == null) {
            System.out.println(name + " not exists");
        } else {
            if (!shell.getState().isStoreable) {
                if (shell.getState().currentTable != null 
                        && shell.getState().currentTable.unsavedChangesCount() != 0) {
                    Utils.generateAnError(shell.getState().currentTable.unsavedChangesCount() 
                            + " unsaved changes", this.getName(), shell.getIsInteractive());
                    return;
                }
                shell.getState().currentTable = shell.getState().tableProvider.getTable(name);
            } else {
                if (shell.getState().currentStoreableTable != null
                        && shell.getState().currentStoreableTable.unsavedChangesCount() != 0) {
                    Utils.generateAnError(shell.getState().currentStoreableTable.unsavedChangesCount()
                            + " unsaved changes", this.getName(), shell.getIsInteractive());
                    return;
                }
                shell.getState().currentStoreableTable 
                = shell.getState().storeableTableProvider.getTable(name);
            }
            System.out.println("using " + name);
        }
    }

}
