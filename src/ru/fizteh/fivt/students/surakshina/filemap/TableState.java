package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;

import ru.fizteh.fivt.students.surakshina.shell.State;

public class TableState extends State {
    private NewTableProvider tableProvider;

    public TableState(File dir, NewTableProvider tableProvider) {
        super(dir);
        this.tableProvider = tableProvider;
    }

    public NewTableProvider getTableProvider() {
        return tableProvider;
    }

    public NewTable getTable() {
        return tableProvider.getNewCurrentTable();
    }
}
