package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.storeable.StoreableTableState;

public class UseCommand extends AbstractCommand {

    public UseCommand(FilesystemState s) {
        super(s, "use", 1);
    }

    public void execute(String[] args) throws IOException {
        FilesystemState table = getState();
        String name = args[1];
        if (table.getWorkingDirectory() != null) {
            int numberOfChanges = table.getNumberOfChanges();
            if (numberOfChanges != 0) {
                getState().getStream().println(numberOfChanges + " unsaved changes");
            } else {
                useTable(name);
            }
        } else {
            useTable(name);
        }
    }
    
    private void useTable(String name) throws IOException {
        FilesystemState table = getState();
        File f = new File(table.provider.getWorkingDirectory(), name);
        if (!f.exists()) {
            getState().getStream().println(name + " not exists");
        } else {
            if (getState().getWorkingDirectory() == null || getState().getName() != null 
                                                 && !name.equals(getState().getName())) {
                table.setWorkingDirectory(f);
                table.setName(name);
                table.provider.use(table);
                StoreableTableState.class.cast(table).clearTable();
                getState().getStream().println("using " + name);
            }
        }
    }

}
