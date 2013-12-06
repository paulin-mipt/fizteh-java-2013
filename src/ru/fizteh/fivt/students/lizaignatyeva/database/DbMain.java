package ru.fizteh.fivt.students.lizaignatyeva.database;


import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandFactory;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandRunner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

public class DbMain {
    static Path directory;
    static CommandFactory factory;

    private static Hashtable<String, Table> tables;
    private static Hashtable<String, Command> commandsMap;

    static Table currentTable = null;

    public static Table getCurrentTable() throws Exception {
        //return databases.get(currentDatabaseName); MULTI ONLY
        return currentTable;
    }

    public static void resetCurrentTable() throws Exception {
        currentTable = null;
    }
    public static Table getTable(String name) throws Exception {
        return tables.get(name);
    }

    public static boolean tableExists(String name) throws Exception {
        return tables.containsKey(name);
    }

    public static void setCurrentTable(String name) throws Exception {
        currentTable = tables.get(name);
    }

    public static void removeTable(String name) throws  Exception {
        tables.remove(name);
    }

    public static void addTable(Table table) throws Exception {

        tables.put(table.name, table);
       // System.out.println("HERE1");
    }

    public static String concatenateWithDelimiter(String[] strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            builder.append(strings[i]);
            if (i != strings.length - 1) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    private static void addCommands() {
        commandsMap = new Hashtable<String, Command>();
        commandsMap.put("put", new PutCommand());
        commandsMap.put("get", new GetCommand());
        commandsMap.put("remove", new RemoveCommand());
        commandsMap.put("exit", new ExitCommand());
        commandsMap.put("use", new UseCommand());
        commandsMap.put("create", new CreateCommand());
        commandsMap.put("drop", new DropCommand());
    }

    private static void scan() {
        for (File subDir : directory.toFile().listFiles()) {
            try {
                Table currTable = new Table(subDir);
                addTable(currTable);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public static void saveCurrentTable() {
        if (currentTable == null) {
            return;
        }
        try {
            currentTable.writeToFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        //TODO: remove that for MULTI
        tables = new Hashtable<String, Table>();
        try {
            String dir = System.getProperty("fizteh.db.dir");
            if (dir == null) {
                throw new Exception("directory not declared");
            }
            directory = Paths.get(dir);
            if (!Files.isDirectory(directory)) {
                throw new Exception(directory + " doesn't exist or is not a directory");
            }
        } catch (Exception e) {
            System.err.println("Error opening database" + (e.getMessage() == null ? "" : (": " + e.getMessage())));
            System.exit(1);
        }
/*
        path = path.resolve("db.dat");

        currentTable = new Database(path.toString()); // SIMPLE ONLY*/
        addCommands();
        scan();
        CommandRunner runner = new CommandRunner(directory.toFile(), commandsMap);
        runner.run(args);

    }
}
