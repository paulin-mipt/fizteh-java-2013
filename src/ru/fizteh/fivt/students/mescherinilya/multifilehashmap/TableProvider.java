package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

import java.io.File;
import java.util.HashMap;

public class TableProvider implements ru.fizteh.fivt.storage.strings.TableProvider {

    File rootDir;
    HashMap<String, Table> tables;

    TableProvider(File dir) {
        rootDir = dir;

        if (rootDir == null) {
            throw new IllegalArgumentException("Root dir is null!");
        }

        if (!rootDir.isDirectory()) {
            throw new IllegalArgumentException("Bad root directory!");
        }

        tables = new HashMap<>();

        for (File cub : rootDir.listFiles()) {
            if (cub.isDirectory()) {
                Table table = new Table(cub.getName(), this);
                tables.put(cub.getName(), table);
            }
        }

    }

    boolean isBadName(String name) {
        if (name == null || name.isEmpty() || name.trim().isEmpty()) {
            return true;
        }

        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (c == '\\' || c == '/' || c == '.' || c == ':' || c == '*'
                    || c == '?' || c == '|' || c == '"' || c == '<' || c == '>'
                    || c == ' ') {
                return true;
            }
        }

        return false;
    }


    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (isBadName(name)) {
            throw new IllegalArgumentException("Bad table name!");
        }

        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {
            return null;
        }

    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException {
        if (isBadName(name)) {
            throw new IllegalArgumentException("Bad table name!");
        }

        if (tables.containsKey(name)) {
            return null;
        } else {
            File path = new File(rootDir.getAbsolutePath() + File.separator + name);
            if (!path.mkdir()) {
                System.err.println("Couldn't create a new directory.");
            }
            Table newTable = new Table(name, this);

            tables.put(name, newTable);
            return newTable;
        }

    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (isBadName(name)) {
            throw new IllegalArgumentException("Bad table name!");
        }

        if (tables.containsKey(name)) {
            File victimTable = new File(rootDir.getAbsoluteFile() + File.separator + name);

            for (Integer i = 0; i < 16; ++i) {
                String dirName = i.toString() + ".dir";

                for (Integer j = 0; j < 16; ++j) {
                    String fileName = dirName + File.separator + j.toString() + ".dat";

                    File victim = new File(victimTable.getAbsoluteFile() + File.separator + fileName);
                    if (victim.exists() && !victim.delete()) {
                        System.err.println("Couldn't delete the file " + fileName);
                    }

                }

                File victimDir = new File(victimTable.getAbsoluteFile() + File.separator + dirName);
                if (victimDir.exists() && !victimDir.delete()) {
                    System.err.println("Couldn't delete the directory " + dirName
                            + ". Maybe there are some unexpected files inside it.");
                }

            }

            if (!victimTable.delete()) {
                System.err.println("Couldn't delete the directory " + name);
            }

            tables.remove(name);

        } else {
            throw new IllegalStateException("The table doesn't exist!");
        }


    }


}
