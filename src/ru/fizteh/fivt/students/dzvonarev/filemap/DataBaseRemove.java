package ru.fizteh.fivt.students.dzvonarev.filemap;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseRemove implements CommandInterface {

    public DataBaseRemove(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException {
        if (tableProvider == null) {
            throw new IOException("can't remove object");
        }
        String tableName = tableProvider.getCurrentTable();
        if (tableName == null) {
            throw new IOException("no table");
        }
        String str = args.get(0);
        int spaceIndex = str.indexOf(' ', 0);
        while (str.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        if (str.indexOf(' ', spaceIndex + 1) != -1) {
            throw new IOException("remove: wrong input");
        }
        String key = str.substring(spaceIndex + 1, str.length());
        MyTable currTable = (MyTable) tableProvider.getTable(tableName);
        Storeable value = currTable.remove(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
