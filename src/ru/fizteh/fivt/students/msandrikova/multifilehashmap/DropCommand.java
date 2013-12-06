package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.IOException;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class DropCommand extends Command {

    public DropCommand() {
        super("drop", 1);
    }

    @Override
    public void execute(String[] argumentsList, Shell shell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
            return;
        }
        
        String name = argumentsList[1];
        
        if (!shell.getState().isStoreable && shell.getState().currentTable != null 
                && shell.getState().currentTable.getName().equals(name)) {
            shell.getState().currentTable = null;
        } 
        if (shell.getState().isStoreable && shell.getState().currentStoreableTable != null 
                && shell.getState().currentStoreableTable.getName().equals(name)) {
            shell.getState().currentStoreableTable = null;
        } 
        
        if (!shell.getState().isStoreable) {
            try {
                shell.getState().tableProvider.removeTable(name);
            } catch (IllegalArgumentException e) {
                Utils.generateAnError(e.getMessage(), this.getName(), shell.getIsInteractive());
                return;
            } catch (IllegalStateException e) {
                System.out.println(name + " not exists");
                return;
            }
        } else {
            try {
                shell.getState().storeableTableProvider.removeTable(name);
            } catch (IOException e) {
                Utils.generateAnError(e.getMessage(), this.getName(), false);
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("wrong type (" + e.getMessage() + ")");
                return;
            } catch (IllegalStateException e) {
                System.out.println(name + " not exists");
                return;
            }
        }
        
        System.out.println("dropped");

    }

}
