package ru.fizteh.fivt.students.dzvonarev.filemap;


import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseCreate implements CommandInterface {

    public DataBaseCreate(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException {
        String str = args.get(0);
        int spaceIndex = str.indexOf(' ', 0);
        while (str.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        int newSpaceIndex = str.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex == -1) {
            throw new IOException("create: wrong parameters");
        }
        int index = newSpaceIndex;
        String newName = str.substring(spaceIndex + 1, index);
        while (str.indexOf(' ', newSpaceIndex + 1) == newSpaceIndex + 1) {
            ++newSpaceIndex;
        }
        String types = str.substring(newSpaceIndex + 1, str.length());
        List<Class<?>> newTypes;
        Parser myParser = new Parser();
        try {
            newTypes = myParser.parseTypeList(myParser.getTypesFrom(types));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        if (tableProvider == null) {
            throw new IOException("can't create table " + newName);
        }
        Table newTable = tableProvider.createTable(newName, newTypes);
        if (newTable == null) {
            System.out.println(newName + " exists");
        } else {
            System.out.println("created");
        }
    }
}
