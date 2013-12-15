package ru.fizteh.fivt.students.mikhaylova_daria.db;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import ru.fizteh.fivt.storage.structured.*;


import ru.fizteh.fivt.students.mikhaylova_daria.shell.Parser;

public class DbMain {

    private static TableData currentTable = null;
    private static TableManager mainManager;

    public static void main(String[] arg) {
        String workingDirectoryName = System.getProperty("fizteh.db.dir");
        if (workingDirectoryName == null) {
            System.err.println("wrong type (Bad property)");
            System.exit(1);
        }
        try {
           mainManager = new TableManager(workingDirectoryName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        HashMap<String, String> commandsList = new HashMap<String, String>();
        commandsList.put("put", "put");
        commandsList.put("get", "get");
        commandsList.put("remove", "remove");
        commandsList.put("exit", "exit");
        commandsList.put("create", "create");
        commandsList.put("use", "use");
        commandsList.put("drop", "drop");
        commandsList.put("commit", "commit");
        commandsList.put("rollback", "rollback");
        commandsList.put("size", "size");

        try {
            try {
                Parser.parser(arg, DbMain.class, commandsList);
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void create(String[] command) throws IllegalArgumentException, IOException {
        if (command.length != 2) {
            throw new IllegalArgumentException("wrong type (create: Wrong number of arguments)");
        }
        String[] arg = command[1].trim().split("\\s+", 2);
        if (arg.length < 2) {
            throw new IllegalArgumentException("wrong type (Wrong number of arguments)");
        }
        String nameDir = arg[0];
        if (!arg[1].startsWith("(")) {
             throw new IllegalArgumentException("wrong type (Not found list of types. Use hooks)");
        }
        String[] args = arg[1].trim().split("[()]");
        if (args.length != 2) { //arg[1] начинается со "(", поэтому отпаршивается пустая строка
            throw new IllegalArgumentException("wrong type (Wrong format of typelist)");
        }
        ArrayList<Class<?>> columnTypes = new ArrayList<>();
        String[] signatures = args[1].trim().split("\\s+");
        for (int i = 0; i < signatures.length; ++i) {
            if (signatures[i].equals("int")) {
                columnTypes.add(i, Integer.class);
            } else if (signatures[i].equals("long")) {
                columnTypes.add(i, Long.class);
            }  else if (signatures[i].equals("byte")) {
                columnTypes.add(i, Byte.class);
            } else if (signatures[i].equals("float")) {
                columnTypes.add(i, Float.class);
            } else  if (signatures[i].equals("double")) {
                columnTypes.add(i, Double.class);
            } else if (signatures[i].equals("boolean")) {
                columnTypes.add(i, Boolean.class);
            } else if (signatures[i].equals("String")) {
                columnTypes.add(i, String.class);
            } else {
                throw new IllegalArgumentException("wrong type (This type is not supposed: "
                        + signatures[i] + ")");
            }
        }

        TableData table = mainManager.createTable(nameDir, columnTypes);
        if (table == null) {
            System.out.println(nameDir + " exists");
        } else {
            System.out.println("created");
        }
    }

    public void drop(String[] command) throws IllegalArgumentException {
        if (command.length != 2) {
            throw new IllegalArgumentException("wrong type (drop: Wrong number of arguments)");
        }
        String nameDir = command[1].trim();
        if (currentTable != null) {
            if (currentTable.tableFile.getName().equals(nameDir)) {
                currentTable = null;
            }
        }
        try {
            mainManager.removeTable(command[1]);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IllegalStateException e) {
             System.out.println(nameDir + " not exists");
        } catch (IOException e) {
            System.out.println("wrong type (Reading/writing error" + e.getMessage() + ")");
        }
        System.out.println("dropped");
    }

    public void use(String[] command) throws IOException {
        if (command.length != 2) {
            throw new IOException("wrong type (use: Wrong number of arguments)");
        }
        String nameDir = command[1].trim();
        TableData buf = mainManager.getTable(nameDir);
        if (buf == null) {
            System.out.println(nameDir + " not exists");
        } else {
            if (currentTable != null) {
                int numberOfChanges = currentTable.countChanges();
                if (numberOfChanges != 0) {
                    System.out.println(numberOfChanges + " unsaved changes");
                } else {
                    System.out.println("using " + nameDir);
                    currentTable = buf;
                }
            } else {
                System.out.println("using " + nameDir);
                currentTable = buf;
            }
        }

    }

    public static void put(String[] command) throws Exception {
        if (currentTable == null) {
            System.out.println("no table");
            System.out.flush();
            return;
        }
        if (command.length != 2) {
            throw new IllegalArgumentException("wrong type (put: Wrong number of arguments)");
        }
        command[1] = command[1].trim();
        String[] arg = command[1].split("\\s+", 2);
        if (arg.length != 2) {
            throw new IllegalArgumentException("wrong type (put: Wrong number of arguments)");
        }
        Storeable oldValue = currentTable.put(arg[0], mainManager.deserialize(currentTable, arg[1]));
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(mainManager.serialize(currentTable, oldValue));
        }
    }

    public static void remove(String[] command) throws IllegalArgumentException {
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        if (command.length != 2) {
            throw new IllegalArgumentException("wrong type (remove: Wrong number of arguments)");
        }
        command[1] = command[1].trim();
        String[] arg = command[1].split("\\s+");
        if (arg.length != 1) {
            throw new IllegalArgumentException("wrong type (remove: Wrong number of arguments)");
        }
        Storeable removedValue = currentTable.remove(arg[0]);
        if (removedValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void get(String[] command) throws IllegalArgumentException {
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        if (command.length != 2) {
            throw new IllegalArgumentException("wrong type (get: Wrong number of arguments)");
        }
        command[1] = command[1].trim();
        String[] arg = command[1].split("\\s+");
        if (arg.length != 1) {
            throw new IllegalArgumentException("wrong type (get: Wrong number of arguments)");
        }
        Storeable value = currentTable.get(arg[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(mainManager.serialize(currentTable, value));
        }
    }

    public static void exit(String[] arg) {
            System.exit(0);
    }

    public static void commit(String[] arg) {
        if (arg.length != 0) {
            if (currentTable == null) {
                System.out.println("no table");
            } else {
                System.out.println(currentTable.commit());
            }
        } else {
            throw new IllegalArgumentException("wrong type (Wrong number of arguments)");
        }
    }

    public static void rollback(String[] arg) {
        if (arg.length != 0) {
            if (currentTable == null) {
                System.out.println("no table");
            } else {
                System.out.println(currentTable.rollback());
            }
        } else {
            throw new IllegalArgumentException("wrong type (Wrong number of arguments)");
        }
    }

    public static void size(String[] arg) {
        if (arg.length != 0) {
            if (currentTable != null) {
                System.out.println(currentTable.size());
            } else {
                System.out.println("no table");
            }
        } else {
            throw new IllegalArgumentException("wrong type (Wrong number of arguments)");
        }
    }

}






