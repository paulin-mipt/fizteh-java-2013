package ru.fizteh.fivt.students.msandrikova.filemap; 

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.storeable.ChangesCountingTable;

public class GetCommand extends Command {

    public GetCommand() {
        super("get", 1);
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
        String value = null;
        
        if (!shell.getState().isStoreable) {
            value = shell.getState().currentTable.get(key);
        } else {
            try {
                ChangesCountingTable curTable = shell.getState().currentStoreableTable;
                value = shell.getState().storeableTableProvider.serialize(curTable, curTable.get(key));
            } catch (IllegalArgumentException e) {
                System.out.println("wrong type (" + e.getMessage() + ")");
                return;
            }
        }
        
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }    
    }
}
