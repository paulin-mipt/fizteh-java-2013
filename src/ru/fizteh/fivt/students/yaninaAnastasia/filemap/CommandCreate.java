package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandCreate extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (args.length == 1) {
            System.out.println("wrong type ( )");
            return false;
        }
        if (args.length > 2 || args.length < 1) {
            System.out.println("wrong type (table has no signature)");
            throw new IllegalArgumentException("Illegal arguments");
        }
        String tempArg = args[1].trim();
        if (!tempArg.contains("(") || !tempArg.contains(")")) {
            throw new IllegalArgumentException("Illegal description of column types");
        }
        String columns = tempArg.substring(1);
        columns = columns.substring(0, columns.length() - 1);
        if (columns.length() == 0) {
            System.out.println("wrong type (wrong type)");
            return false;
        }
        String[] arrColumns = columns.split("\\s+");
        List<Class<?>> cols = new ArrayList<Class<?>>();
        for (int i = 0; i < arrColumns.length; i++) {
            cols.add(ColumnTypes.fromNameToType(arrColumns[i]));
        }
        if (cols.isEmpty()) {
            System.out.println("wrong type (column types cannot be null)");
            return false;
        }
        String path = myState.getProperty(myState);
        if (myState.database.tables.containsKey(args[0])) {
            System.out.println(args[0] + " exists");
            return false;
        }
        File temp = new File(path, args[0]);
        if (!temp.exists()) {
            temp.mkdir();
        } else {
            System.out.println(args[0] + " exists");
            return false;
        }
        myState.database.createTable(args[0], cols);
        System.out.println("created");
        return true;
    }

    public String getCmd() {
        return "create";
    }
}
