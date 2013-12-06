package ru.fizteh.fivt.students.msandrikova.filemap;

import java.text.ParseException;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;
import ru.fizteh.fivt.students.msandrikova.storeable.ChangesCountingTable;

public class PutCommand extends Command {

    public PutCommand() {
        super("put", 2);
    }

    @Override
    public void execute(String[] argumentsList, Shell shell) {
        if (!shell.getState().isStoreable && !super.getArgsAcceptor(argumentsList.length - 1,
                shell.getIsInteractive())) {
            return;
        }
        
        if (shell.getState().isStoreable && argumentsList.length < 3) {
            Utils.generateAnError("Incorrect arguments amount.", this.getName(), shell.getIsInteractive());
            return;
        }
        String key = argumentsList[1];
        String jsonArgument = null;
        
        if (shell.getState().isStoreable) {
            jsonArgument = shell.getCurrentInstruction().trim().substring(3).trim();
            jsonArgument = jsonArgument.substring(key.length()).trim();
            argumentsList[2] = jsonArgument;
        }
        
        
        String value = argumentsList[2];
        
        if (!Utils.testUTFSize(key) || !Utils.testUTFSize(value)) {
            Utils.generateAnError("Key and value can not be grater than 1 MB.", this.getName(), 
                    shell.getIsInteractive());
            return;
        }
        
        if ((shell.getState().isMultiFileHashMap && shell.getState().currentTable == null) 
                || (shell.getState().isStoreable && shell.getState().currentStoreableTable == null)) {
            System.out.println("no table");
            return;
        }
        
        String oldValue = null;
        
        if (!shell.getState().isStoreable) {
            oldValue = shell.getState().currentTable.put(key, value);
        } else {
            Storeable storeableValue = null;
            ChangesCountingTable curTable = shell.getState().currentStoreableTable;
            try {
                storeableValue = shell.getState().storeableTableProvider.deserialize(curTable, value);
            } catch (ParseException e) {
                System.out.println("wrong type (" + e.getMessage() + ")");
                return;
            }
            try {
                oldValue = shell.getState().storeableTableProvider.serialize(curTable, 
                        curTable.put(key, storeableValue));
            } catch (IllegalArgumentException e) {
                System.out.println("wrong type (" + e.getMessage() + ")");
                return;
            }
        }

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }

    }

}
