package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import java.io.File;

public class MultiFileHashMapMain {
    public static void main(String[] args) {
        try {
            File dbDir = new File(System.getProperty("fizteh.db.dir"));

            dbDir = dbDir.getCanonicalFile();

            if (!dbDir.isDirectory()) {
                System.err.println("ERROR: incorrect database directory format\n");
                System.exit(1);
            }

            if (dbDir.listFiles() != null) {
                for (File tableDir : dbDir.listFiles()) {
                    if (!tableDir.isDirectory()) {
                        System.err.println("ERROR: incorrect table format\n");
                        System.exit(1);
                    }
                }
            }

            AbstractMultiFileHashMap multiFileHashMap = new AbstractMultiFileHashMap(dbDir);

            if (args.length != 0) {
                multiFileHashMap.batchMode(args);
            } else {
                multiFileHashMap.interactiveMode();
            }
        } catch (NullPointerException e) {
            System.err.println("ERROR: cannot get property\n");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: incorrect directory\n");
            System.exit(1);
        }

        System.exit(0);
    }
}
