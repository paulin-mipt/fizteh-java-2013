package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vorotilov.shell.ConsoleInput;
import ru.fizteh.fivt.students.vorotilov.shell.ExitCommand;
import ru.fizteh.fivt.students.vorotilov.shell.NoNextCommand;
import ru.fizteh.fivt.students.vorotilov.shell.WrongCommand;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class StoreableTableMain {
    private static boolean interactiveMode;
    private static StoreableTableProvider tableProvider;
    private static StoreableTable currentTable;

    private static void processCommand(String[] parsedCommand) throws ExitCommand, IOException, WrongCommand {
        try {
            switch (parsedCommand[0]) {
                case "exit":
                    if (parsedCommand.length > 1) {
                        System.out.println("exit: must not get parameter");
                        throw new WrongCommand();
                    } else {
                        throw new ExitCommand();
                    }
                case "create":
                    if (parsedCommand.length != 3) {
                        System.out.println("create: must get 2 parameters");
                        throw new WrongCommand();
                    } else {
                        List<Class<?>> columTypes = SignatureFile.parseInputColumnTypes(parsedCommand[2]);
                        StoreableTable newTable = tableProvider.createTable(parsedCommand[1], columTypes);
                        if (newTable == null) {
                            System.out.println(parsedCommand[1] + " exists");
                        } else {
                            System.out.println("created");
                        }
                    }
                    break;
                case "use":
                    if (parsedCommand.length != 2) {
                        System.out.println("use: must get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        StoreableTable newTable = tableProvider.getTable(parsedCommand[1]);
                        if (newTable == null) {
                            System.out.println(parsedCommand[1] + " not exists");
                            throw new WrongCommand();
                        } else {
                            if (currentTable != null) {
                                if (currentTable.getName().equals(newTable.getName())) {
                                    System.out.println(parsedCommand[1] + " is already used");
                                } else {
                                    if (currentTable.uncommittedChanges() == 0) {
                                        try {
                                            currentTable.close();
                                        } catch (Exception e) {
                                            throw new IllegalStateException("Can't close table", e);
                                        }
                                        currentTable = newTable;
                                        System.out.println("using " + parsedCommand[1]);
                                    } else {
                                        System.out.println(Integer.toString(currentTable.uncommittedChanges())
                                                + " unsaved changes");
                                    }
                                }
                            } else {
                                currentTable = newTable;
                                System.out.println("using " + parsedCommand[1]);
                            }
                        }
                    }
                    break;
                case "drop":
                    if (parsedCommand.length != 2) {
                        System.out.println("drop: must get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        File tablePath = new File(tableProvider.getRoot(), parsedCommand[1]);
                        if (!tablePath.exists()) {
                            System.out.println(parsedCommand[1] + " not exists");
                            throw new WrongCommand();
                        } else {
                            tableProvider.removeTable(parsedCommand[1]);
                            System.out.println("dropped");
                            if (currentTable != null && currentTable.getName().equals(parsedCommand[1])) {
                                currentTable = null;
                            }
                        }
                    }
                    break;
                case "put":
                    if (parsedCommand.length != 3) {
                        System.out.println("put: must get 2 parameters");
                        throw new WrongCommand();
                    } else if (currentTable == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        Storeable value;
                        try {
                            value = currentTable.put(parsedCommand[1],
                                    tableProvider.deserialize(currentTable, parsedCommand[2]));
                        } catch (ParseException e) {
                            throw new ColumnFormatException("parse exception", e);
                        }
                        if (value == null) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite\n" + value);
                        }
                    }
                    break;
                case "get":
                    if (parsedCommand.length != 2) {
                        System.out.println("get: must get 1 parameter");
                        throw new WrongCommand();
                    } else if (currentTable == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        Storeable value = currentTable.get(parsedCommand[1]);
                        if (value != null) {
                            System.out.println("found\n" + value);
                        } else {
                            System.out.println("not found");
                        }
                    }
                    break;
                case "size":
                    if (parsedCommand.length != 1) {
                        System.out.println("size: must not get parameter");
                        throw new WrongCommand();
                    } else if (currentTable == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        System.out.println(currentTable.size());
                    }
                    break;
                case "commit":
                    if (parsedCommand.length != 1) {
                        System.out.println("commit: must not get parameter");
                        throw new WrongCommand();
                    } else if (currentTable == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        System.out.println(currentTable.commit());
                    }
                    break;
                case "rollback":
                    if (parsedCommand.length != 1) {
                        System.out.println("rollback: must not get parameter");
                        throw new WrongCommand();
                    } else if (currentTable == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        System.out.println(currentTable.rollback());
                    }
                    break;
                case "remove":
                    if (parsedCommand.length != 2) {
                        System.out.println("remove: must get 1 parameter");
                        throw new WrongCommand();
                    } else if (currentTable == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        Storeable value = currentTable.remove(parsedCommand[1]);
                        if (value != null) {
                            System.out.println("removed");
                        } else {
                            System.out.println("not found");
                        }
                    }
                    break;
                case "":
                    throw new WrongCommand();
                default:
                    System.out.println("unknown command: '" + parsedCommand[0] + "'");
                    throw new WrongCommand();
            }
        } catch (WrongCommand e) {
            if (!interactiveMode) {
                throw e;
            }
        } catch (ColumnFormatException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
            if (!interactiveMode) {
                throw e;
            }
        }
    }

    public static void main(String[] args) {
        interactiveMode = (args.length == 0);
        try {
            StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
            String rootDir = System.getProperty("fizteh.db.dir");
            if (rootDir == null) {
                throw new IllegalArgumentException("Property is null");
            }
            tableProvider = tableProviderFactory.create(rootDir);
            ConsoleInput shellInputCommands;
            if (interactiveMode) {
                shellInputCommands = new DbInteractiveInput();
            } else {
                shellInputCommands = new DbPackageInput(args);
            }
            while (shellInputCommands.hasNext()) {
                processCommand(shellInputCommands.getNext());
            }
        } catch (ExitCommand | NoNextCommand e) {
            if (currentTable != null) {
                try {
                    currentTable.commit();
                    currentTable.close();
                } catch (Exception f) {
                    throw new RuntimeException();
                }
            }
            System.exit(0);
        } catch (IOException | WrongCommand | RuntimeException e) {
            if (currentTable != null) {
                try {
                    currentTable.close();
                } catch (Exception f) {
                    throw new RuntimeException();
                }
            }
            System.exit(1);
        }
    }

}
