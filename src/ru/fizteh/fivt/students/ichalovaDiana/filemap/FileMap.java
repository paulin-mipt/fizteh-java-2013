package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length < ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String tableName = arguments[1];
                
                //TODO: rewrite!!!
                
                List<Class<?>> columnTypes = new ArrayList<Class<?>>();
                for (int i = 2; i < arguments.length; ++i) {
                    if (i == 2) {
                        arguments[i] = arguments[i].substring(1);
                    }
                    if (i == arguments.length - 1) {
                        arguments[i] = arguments[i].substring(0, arguments[i].length() - 1);
                    }
                    columnTypes.add(forName(arguments[i]));
                }
                
                
                Table newTable = database.createTable(tableName, columnTypes);
                
                if (newTable == null) {
                    System.out.println(tableName + " exists");
                } else {
                    System.out.println("created");
                }

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
        
        private static Class<?> forName(String className) {
            Map<String, Class<?>> types = new HashMap<String, Class<?>>();
            types.put("int", int.class);
            types.put("long", long.class);
            types.put("byte", byte.class);
            types.put("float", float.class);
            types.put("double", double.class);
            types.put("boolean", boolean.class);
            types.put("String", String.class);
            
            return types.get(className);
        }
    }

    static class Drop extends Command {
        static final int ARG_NUM = 2;

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
                }
                System.out.println("dropped");
                
                if (FileMap.currentTableName.equals(tableName)) {
                    FileMap.currentTableName = null;
                }

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }

    static class Use extends Command {
        static final int ARG_NUM = 2;

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
        static final int ARG_NUM = 3;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length < ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String key = arguments[1];

                StringBuilder concatArgs = new StringBuilder();
                for (int i = 2; i < arguments.length; ++i) {
                    concatArgs.append(arguments[i]).append(" ");
                }
                String value = concatArgs.toString();
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }
                
                Storeable oldValueStoreable = table.put(key, FileMap.database.deserialize(table, value));
                
                String oldValue = FileMap.database.serialize(table, oldValueStoreable);
                if (oldValue != null) {
                    System.out.println("overwrite");
                    System.out.println(oldValue);
                } else {
                    System.out.println("new");
                }
                
            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }

    static class Get extends Command {
        static final int ARG_NUM = 2;

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
