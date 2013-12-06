package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class SizeCommand extends Command {

    public SizeCommand() {
        super("size", 0);
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
        
        int size = 0;
        if (!shell.getState().isStoreable) {
            size = shell.getState().currentTable.size();
        } else {
            size = shell.getState().currentStoreableTable.size();
        }
        
        System.out.println(size);
    }

}
