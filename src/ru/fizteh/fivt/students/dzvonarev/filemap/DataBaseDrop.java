package ru.fizteh.fivt.students.dzvonarev.filemap;


import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseDrop implements CommandInterface {

    public DataBaseDrop(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException, IllegalStateException {
        String str = args.get(0);
        int spaceIndex = str.indexOf(' ', 0);
        if (spaceIndex == -1) {
            throw new IOException("drop: wrong parameters");
        }
        while (str.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        if (str.indexOf(' ', spaceIndex + 1) != -1) {
            throw new IOException("drop: wrong parameters");
        }
        String table = str.substring(spaceIndex + 1, str.length());
        if (tableProvider == null) {
            throw new IOException("can't drop table " + table);
        }
        tableProvider.removeTable(table);
        System.out.println("dropped");
    }
}
