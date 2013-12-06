package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class DataBasePut implements CommandInterface {

    public DataBasePut(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException {
        if (tableProvider == null) {
            throw new IOException("can't put object");
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
        int newSpaceIndex = str.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex == -1) {
            throw new IOException("put: wrong parameters");
        }
        int index = newSpaceIndex;
        String key = str.substring(spaceIndex + 1, index);
        while (str.indexOf(' ', newSpaceIndex + 1) == newSpaceIndex + 1) {
            ++newSpaceIndex;
        }
        String value = str.substring(newSpaceIndex + 1, str.length());
        Storeable valueStoreable;
        try {
            valueStoreable = tableProvider.deserialize(tableProvider.getTable(tableName), value);
        } catch (ParseException e) {
            throw new IOException(e);
        }
        MyTable currTable = (MyTable) tableProvider.getTable(tableName);
        Storeable result;
        try {
            result = currTable.put(key, valueStoreable);
        } catch (ColumnFormatException e) {
            throw new IOException(e);
        }
        if (result != null) {
            String oldValue = tableProvider.serialize(tableProvider.getTable(tableName), result);
            System.out.println("overwrite");
            System.out.println(oldValue);
        } else {
            System.out.println("new");
        }
    }

}
