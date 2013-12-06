package ru.fizteh.fivt.students.dobrinevski.jUnit;

import java.io.File;
import java.util.HashMap;

import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MyMultiHashMap;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MultiFileHashMapCommands;
import ru.fizteh.fivt.students.dobrinevski.shell.Shell;
import ru.fizteh.fivt.students.dobrinevski.shell.Command;

public class Main {
    private static MyMultiHashMap dtb;
    private static HashMap<String, Command> cmdMap = new HashMap<String, Command>();

    static {
        try {
            String way = System.getProperty("fizteh.db.dir");
            if (way == null) {
                throw new Exception("Illegal way");
            }
            File dbsDir = new File(way);
            if (!dbsDir.isDirectory()) {
                throw new Exception(dbsDir + " doesn't exist or is not a directory");
            }
            dtb = new MyMultiHashMap();
            cmdMap.put("create", new MultiFileHashMapCommands.Create(dtb, dbsDir));
            cmdMap.put("rollback", new TransMultiFileHashMapCommands.RollBack(dtb, dbsDir));
            cmdMap.put("commit", new TransMultiFileHashMapCommands.Commit(dtb, dbsDir));
            cmdMap.put("size", new TransMultiFileHashMapCommands.Size(dtb, dbsDir));
            cmdMap.put("drop", new MultiFileHashMapCommands.Drop(dtb, dbsDir));
            cmdMap.put("use", new TransMultiFileHashMapCommands.Use(dtb, dbsDir));
            cmdMap.put("put", new MultiFileHashMapCommands.Put(dtb, dbsDir));
            cmdMap.put("get", new MultiFileHashMapCommands.Get(dtb, dbsDir));
            cmdMap.put("remove", new MultiFileHashMapCommands.Remove(dtb, dbsDir));
            cmdMap.put("exit", new TransMultiFileHashMapCommands.Exit(dtb, dbsDir));

        } catch (Exception e) {
            System.out.println("Error while opening database: " + (e.getMessage()));
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        Shell sl = new Shell(cmdMap, "fizteh.db.dir");
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg).append(' ');
            }
            try {
                sl.executeCommands(builder.toString());
            } catch (Exception e) {
                System.err.println(e);
                System.exit(1);
            }
        } else {
            sl.iMode();
        }
    }
}

