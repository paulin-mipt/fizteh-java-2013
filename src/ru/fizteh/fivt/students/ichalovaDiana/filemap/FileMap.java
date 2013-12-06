package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.ichalovaDiana.shell.Command;
import ru.fizteh.fivt.students.ichalovaDiana.shell.Interpreter;

public class FileMap {

    private static Map<String, Command> commands = new HashMap<String, Command>();
    private static Interpreter interpreter;
    
    private static TableProvider database;
    private static TableImplementation table;
    private static String currentTableName; // delete?

    static {
        try {
            String dbDir = System.getProperty("fizteh.db.dir");

            TableProviderFactory factory = new TableProviderFactoryImplementation();
            database = factory.create(dbDir);
            
        } catch (Exception e) {
            System.out.println(((e.getMessage() != null) ? e.getMessage() : "unknown error"));
            System.exit(1);
        }

        commands.put("create", new Create());
        commands.put("drop", new Drop());
        commands.put("use", new Use());
        commands.put("put", new Put());
        commands.put("get", new Get());
        commands.put("remove", new Remove());
        commands.put("commit", new Commit());
        commands.put("rollback", new Rollback());
        commands.put("size", new Size());
        commands.put("exit", new Exit());

        interpreter = new Interpreter(commands);
    }

    public static void main(String[] args) {
        try {
            interpreter.run(args);
        } catch (Exception e) {
            System.out.println("Error while running: " + e.getMessage());
        }
    }

    static class Create extends Command {
        static final int ARG_NUM = 2;
        public boolean rawArgumentsNeeded = true;
        
        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String[] parsedArguments = arguments[1].split("\\s+", 2);
                if (parsedArguments.length != 2) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }
                
                String tableName = parsedArguments[0];
                
                if (!parsedArguments[1].matches("\\(([A-Za-z]+\\s*)*\\)")) {
                    throw new ColumnFormatException("Invalid column types");
                }
                String[] types = parsedArguments[1].substring(1, parsedArguments[1].length() - 1).split("\\s+");
                
                List<Class<?>> columnTypes = new ArrayList<Class<?>>();
                for (int i = 0; i < types.length; ++i) {
                    columnTypes.add(forName(types[i]));
                }
                
                
                Table newTable = database.createTable(tableName, columnTypes);
                
                if (newTable == null) {
                    System.out.println(tableName + " exists");
                } else {
                    System.out.println("created");
                }
            
            } catch (ColumnFormatException e) {
                throw new ColumnFormatException("wrong type (" + e.getMessage() + ")", e);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        
        private static Class<?> forName(String className) {
            Map<String, Class<?>> types = new HashMap<String, Class<?>>();
            types.put("int", Integer.class);
            types.put("long", Long.class);
            types.put("byte", Byte.class);
            types.put("float", Float.class);
            types.put("double", Double.class);
            types.put("boolean", Boolean.class);
            types.put("String", String.class);
            
            return types.get(className);
        }
    }

    static class Drop extends Command {
        static final int ARG_NUM = 2;
        public boolean rawArgumentsNeeded = false;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String tableName = arguments[1];
                
                try {
                    database.removeTable(tableName);
                } catch (IllegalStateException e) {
                    System.out.println(tableName + " not exists");
                    return;
                }
                System.out.println("dropped");
                
                if (currentTableName != null && FileMap.currentTableName.equals(tableName)) {
                    FileMap.currentTableName = null;
                }

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }

    static class Use extends Command {
        static final int ARG_NUM = 2;
        public boolean rawArgumentsNeeded = false;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }
                
                String tableName = arguments[1];
                
                if (FileMap.table != null) {
                    int changesNumber = FileMap.table.countChanges();
                    if (changesNumber > 0) {
                        System.out.println(changesNumber + " unsaved changes");
                        return;
                    }
                }
                        
                TableImplementation tempTable = (TableImplementation) database.getTable(tableName); // what can i do?
                
                if (tempTable == null) {
                    System.out.println(tableName + " not exists");
                } else {
                    FileMap.currentTableName = tableName;
                    FileMap.table = tempTable;
                    System.out.println("using " + tableName);
                }

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }
    

    static class Put extends Command {
        static final int ARG_NUM = 2;
        public boolean rawArgumentsNeeded = true;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }
                
                String[] parsedArguments = arguments[1].trim().split("\\s+", 2);
                if (parsedArguments.length != 2) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }
                
                String key = parsedArguments[0];
                String value = parsedArguments[1];
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }
                
                Storeable oldValueStoreable;
                try {
                    oldValueStoreable = table.put(key, FileMap.database.deserialize(table, value));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("wrong type (" + e.getMessage() + ")", e);
                }
                
                String oldValue = FileMap.database.serialize(table, oldValueStoreable);
                if (oldValue != null) {
                    System.out.println("overwrite");
                    System.out.println(oldValue);
                } else {
                    System.out.println("new");
                }
                
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    static class Get extends Command {
        static final int ARG_NUM = 2;
        public boolean rawArgumentsNeeded = false;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String key = arguments[1];
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }

                String value = FileMap.database.serialize(table, table.get(key));

                if (value != null) {
                    System.out.println("found");
                    System.out.println(value);
                } else {
                    System.out.println("not found");
                }

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }

    static class Remove extends Command {
        static final int ARG_NUM = 2;
        public boolean rawArgumentsNeeded = false;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String key = arguments[1];
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }

                String value = FileMap.database.serialize(table, table.remove(key));

                if (value != null) {
                    System.out.println("removed");
                } else {
                    System.out.println("not found");
                }

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }
    
    static class Commit extends Command {
        static final int ARG_NUM = 1;
        public boolean rawArgumentsNeeded = false;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }

                int changesNumber = table.commit();
                
                System.out.println(changesNumber);

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }
    
    static class Rollback extends Command {
        static final int ARG_NUM = 1;
        public boolean rawArgumentsNeeded = false;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }

                int changesNumber = table.rollback();
                
                System.out.println(changesNumber);

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }
    
    static class Size extends Command {
        static final int ARG_NUM = 1;
        public boolean rawArgumentsNeeded = false;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }

                int size = table.size();
                
                System.out.println(size);

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }

    static class Exit extends Command {
        static final int ARG_NUM = 1;
        public boolean rawArgumentsNeeded = false;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                System.out.println("exit");
                System.exit(0);

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }
    
}
