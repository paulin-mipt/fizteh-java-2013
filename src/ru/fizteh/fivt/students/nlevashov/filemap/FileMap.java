package ru.fizteh.fivt.students.nlevashov.filemap;

import ru.fizteh.fivt.students.nlevashov.mode.Mode;
import ru.fizteh.fivt.students.nlevashov.shell.Shell;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.nlevashov.factory.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.io.IOException;
import java.util.regex.Pattern;

public class FileMap {

    static Table currentTable = null;
    static TableProvider provider;

    public static void create(String tableName, String[] types) throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        for (int j = 0; j < types.length; ++j) {
            switch (types[j]) {
                case "int":
                    classes.add(Integer.class);
                    break;
                case "long":
                    classes.add(Long.class);
                    break;
                case "byte":
                    classes.add(Byte.class);
                    break;
                case "float":
                    classes.add(Float.class);
                    break;
                case "double":
                    classes.add(Double.class);
                    break;
                case "boolean":
                    classes.add(Boolean.class);
                    break;
                case "String":
                    classes.add(String.class);
                    break;
                default:
                    throw new IOException("wrong type (illegal type/types)");
            }
        }
        try {
            if (provider.createTable(tableName, classes) == null) {
                throw new IOException(tableName + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("wrong type (" + e.getMessage() + ")");
        }
    }

    public static void drop(String tableName) throws IOException {
        try {
            provider.removeTable(tableName);
            if (tableName.equals(currentTable.getName())) {
                currentTable = null;
            }
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            throw new IOException(tableName + " not exists");
        }
    }

    public static void use(String tableName) throws IOException {
        Table newCurrentTable = provider.getTable(tableName);
        if (newCurrentTable == null) {
            throw new IOException(tableName + " not exists");
        } else {
            if (currentTable == null) {
                currentTable = newCurrentTable;
                System.out.println("using " + tableName);
            } else {
                MyTable currentTableCopy = (MyTable) currentTable;
                int difference = currentTableCopy.threadSafeDifference();
                if (difference > 0) {
                    System.out.println(difference + " unsaved changes");
                } else {
                    currentTable = newCurrentTable;
                    System.out.println("using " + tableName);
                }
            }
        }
    }

    public static void put(String key, String value) throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        try {
            Storeable putStorable = currentTable.put(key, provider.deserialize(currentTable, value));
            if (putStorable == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(provider.serialize(currentTable, putStorable));
            }
        } catch (ParseException e) {
            throw new IOException("wrong type (parsing error in value at " + e.getErrorOffset() + " position)");
        }
    }

    public static void get(String key) throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        Storeable getStorable = currentTable.get(key);
        if (getStorable == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(provider.serialize(currentTable, getStorable));
        }
    }

    public static void remove(String key) throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        if (currentTable.remove(key) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void size() throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        System.out.println(currentTable.size());
    }

    public static void commit() throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        System.out.println(currentTable.commit());
    }

    public static void rollback() throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        System.out.println(currentTable.rollback());
    }

    public static void main(String[] args) {
        try {
            String addr = System.getProperty("fizteh.db.dir");
            if (addr == null) {
                System.err.println("Property \"fizteh.db.dir\" wasn't set");
                System.exit(1);
            }
            Path addrPath = Shell.makePath(addr).toPath();
            TableProviderFactory factory = new MyTableProviderFactory();
            provider = factory.create(addrPath.toString());
            Mode.start(args, new Mode.Executor() {
                public boolean execute(String cmd) throws IOException {
                    if (!cmd.isEmpty()) {
                        int firstSpace = cmd.indexOf(' ');
                        String commandName;
                        String arguments;
                        if (firstSpace == -1) {
                            commandName = cmd;
                            arguments = "";
                        } else {
                            commandName = cmd.substring(0, firstSpace);
                            arguments = cmd.substring(firstSpace + 1);
                        }
                        switch (commandName) {
                            case "create": {
                                if (!Pattern.compile("[^/:\\*\\?\"\\\\><\\|\\t\\n]+\\s\\([A-Za-z\\s]+\\)$")
                                            .matcher(arguments).matches()) {
                                //if (!Pattern.compile("([a-z]+)\\s\\([a-z\\s]+\\)").matcher(arguments).matches()) {
                                    throw new IOException("wrong type (create: wrong arguments)");
                                }
                                int bracketIndex = arguments.indexOf(' ');
                                create(arguments.substring(0, bracketIndex),
                                       arguments.substring(bracketIndex + 2,
                                                           arguments.length() - 1).split("[\\s\\t\\n]"));
                                break;
                            }
                            case "drop": {
                                int space = arguments.indexOf(' ');
                                if (space != -1) {
                                    throw new IOException("wrong type (drop: wrong arguments number)");
                                }
                                drop(arguments);
                                break;
                            }
                            case "use": {
                                int space = arguments.indexOf(' ');
                                if (space != -1) {
                                    throw new IOException("wrong type (use: wrong arguments number)");
                                }
                                use(arguments);
                                break;
                            }
                            case "put": {
                                int space = arguments.indexOf(' ');
                                if (space == -1) {
                                    throw new IOException("wrong type (put: wrong arguments number)");
                                }
                                put(arguments.substring(0, space), arguments.substring(space + 1));
                                break;
                            }
                            case "get": {
                                int space = arguments.indexOf(' ');
                                if (space != -1) {
                                    throw new IOException("wrong type (get: wrong arguments number)");
                                }
                                get(arguments);
                                break;
                            }
                            case "remove": {
                                int space = arguments.indexOf(' ');
                                if (space != -1) {
                                    throw new IOException("wrong type (remove: wrong arguments number)");
                                }
                                remove(arguments);
                                break;
                            }
                            case "size": {
                                if (!arguments.isEmpty()) {
                                    throw new IOException("wrong type (size: wrong arguments number)");
                                }
                                size();
                                break;
                            }
                            case "commit": {
                                if (!arguments.isEmpty()) {
                                    throw new IOException("wrong type (commit: wrong arguments number)");
                                }
                                commit();
                                break;
                            }
                            case "rollback": {
                                if (!arguments.isEmpty()) {
                                    throw new IOException("wrong type (rollback: wrong arguments number)");
                                }
                                rollback();
                                break;
                            }
                            case "exit":
                                return false;
                            default:
                                throw new IOException("wrong type (wrong command: " + cmd + ")");
                        }
                    }
                    return true;
                }
            });
        } catch (IOException | IllegalStateException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
