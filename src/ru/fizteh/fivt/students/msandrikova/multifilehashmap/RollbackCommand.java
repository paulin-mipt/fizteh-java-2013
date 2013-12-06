package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class RollbackCommand extends Command {

    public RollbackCommand() {
        super("rollback", 0);
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
        
        int changesCount = 0;
        if (!shell.getState().isStoreable) {
            changesCount = shell.getState().currentTable.rollback();
        } else {
            changesCount = shell.getState().currentStoreableTable.rollback();
        }
        System.out.println(changesCount);
    }

}
