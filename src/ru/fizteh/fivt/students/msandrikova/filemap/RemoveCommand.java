package ru.fizteh.fivt.students.msandrikova.filemap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.storeable.ChangesCountingTable;

public class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove", 1);
    }

    
    @Override
    public void execute(String[] argumentsList, Shell shell) {
        if (!super.getArgsAcceptor(argumentsList.length - 1, shell.getIsInteractive())) {
            return;
        }

        if ((shell.getState().isMultiFileHashMap && shell.getState().currentTable == null) 
                || (shell.getState().isStoreable && shell.getState().currentStoreableTable == null)) {
            System.out.println("no table");
            return;
        }
        
        String key = argumentsList[1];
        String oldValue = null;
        
        if (!shell.getState().isStoreable) {
            oldValue = shell.getState().currentTable.remove(key);
        } else {
            try {
                ChangesCountingTable curTable = shell.getState().currentStoreableTable;
                oldValue = shell.getState().storeableTableProvider.serialize(curTable, 
                        curTable.remove(key));
            } catch (IllegalArgumentException e) {
                System.out.println("wrong type (" + e.getMessage() + ")");
                return;
            }
        }

        if (oldValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
        
    }

}
