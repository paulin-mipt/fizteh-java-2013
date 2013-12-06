package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.IOException;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class CommitCommand extends Command {

    public CommitCommand() {
        super("commit", 0);
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
            changesCount = shell.getState().currentTable.commit();
        } else {
            try {
                changesCount = shell.getState().currentStoreableTable.commit();
            } catch (IOException e) {
                Utils.generateAnError(e.getMessage() , this.getName(), false);
            }
        }
        
        System.out.println(changesCount);
    }

}
