package ru.fizteh.fivt.students.dzvonarev.filemap;


import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseUse implements CommandInterface {

    public DataBaseUse(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException {
        String str = args.get(0);
        int spaceIndex = str.indexOf(' ', 0);
        if (spaceIndex == -1) {
            throw new IOException("use: wrong parameters");
        }
        while (str.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        if (str.indexOf(' ', spaceIndex + 1) != -1) {
            throw new IOException("use: wrong parameters");
        }
        String tableName = str.substring(spaceIndex + 1, str.length());
        if (tableProvider == null) {
            throw new IOException("can't use table");
        }
        if (tableProvider.getCurrentTable() != null) {
            MyTable currTable = (MyTable) tableProvider.getTable(tableProvider.getCurrentTable());
            int countOfChanges = currTable.getCountOfChanges();
            if (countOfChanges != 0) {
                System.out.println(currTable.getCountOfChanges() + " unsaved changes");
            } else {
                if (tableProvider.changeCurrentTable(tableName) == -1) {
                    throw new IOException(tableName + " not exists");
                } else {
                    System.out.println("using " + tableName);
                }
            }
        } else {
            if (tableProvider.changeCurrentTable(tableName) == -1) {
                throw new IOException(tableName + " not exists");
            } else {
                System.out.println("using " + tableName);
            }
        }
    }

}
