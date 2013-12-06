package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseRollback implements CommandInterface {

    public DataBaseRollback(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException {
        if (tableProvider == null) {
            throw new IOException("can't do rollback");
        }
        String tableName = tableProvider.getCurrentTable();
        if (tableName == null) {
            throw new IOException("no table");
        }
        MyTable currTable = (MyTable) tableProvider.getTable(tableName);
        int changes;
        try {
            changes = currTable.rollback();
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException(e);
        }
        System.out.println(changes);
    }
}
