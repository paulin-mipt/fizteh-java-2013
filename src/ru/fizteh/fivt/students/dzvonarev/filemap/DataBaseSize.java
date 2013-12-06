package ru.fizteh.fivt.students.dzvonarev.filemap;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseSize implements CommandInterface {

    public DataBaseSize(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException {
        if (tableProvider == null) {
            throw new IOException("can't get size of table");
        }
        String tableName = tableProvider.getCurrentTable();
        if (tableName == null) {
            throw new IOException("no table");
        }
        int size;
        try {
            size = tableProvider.getSize();
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException(e);
        }
        System.out.println(size);
    }

}
