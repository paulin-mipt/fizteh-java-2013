package ru.fizteh.fivt.students.fedoseev.storeable;

import java.io.File;

public class StoreableMain {
    private static void checkDbDirectory(File dbDir) {
        if (!dbDir.isDirectory()) {
            System.err.println("ERROR: incorrect database directory format");
            System.exit(1);
        }

        if (dbDir.listFiles() != null) {
            for (File tableDir : dbDir.listFiles()) {
                if (!tableDir.isDirectory()) {
                    System.err.println("ERROR: incorrect table format");
                    System.exit(1);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            File dbDir = new File(System.getProperty("fizteh.db.dir"));

            dbDir = dbDir.getCanonicalFile();

            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            checkDbDirectory(dbDir);

            AbstractStoreable storable = new AbstractStoreable(dbDir);

            if (args.length != 0) {
                storable.batchMode(args);
            } else {
                storable.interactiveMode();
            }
        } catch (NullPointerException e) {
            System.err.println("ERROR: cannot get property");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: incorrect directory");
            System.exit(1);
        }

        System.exit(0);
    }
}
