package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseGet implements CommandInterface {

    public DataBaseGet(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException {
        if (tableProvider == null) {
            throw new IOException("can't get object");
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
            throw new IOException("get: wrong input");
        }
        String key = str.substring(spaceIndex + 1, str.length());
        MyTable currTable = (MyTable) tableProvider.getTable(tableName);
        Storeable value = currTable.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            String foundValue = tableProvider.serialize(tableProvider.getTable(tableName), value);
            System.out.println("found");
            System.out.println(foundValue);
        }
    }

}
