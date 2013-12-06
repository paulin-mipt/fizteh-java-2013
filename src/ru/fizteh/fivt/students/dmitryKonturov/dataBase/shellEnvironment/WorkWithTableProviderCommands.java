package ru.fizteh.fivt.students.dmitryKonturov.dataBase.shellEnvironment;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableImplementation;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils.StoreableUtils;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellCommand;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellInfo;

import java.util.ArrayList;
import java.util.List;

public class WorkWithTableProviderCommands {

    private static void checkAndGetArguments(String[] args, int argNum, String[] realArgs) throws ShellException {
        if (argNum == 0 && args.length != 0) {
            throw new ShellException("Too much arguments");
        }
        if (args.length != 1) {
            throw new ShellException("Invalid arguments");
        }
        if (argNum > 2) {
            throw new ShellException("Could not split arguments");
        }
        String[] tmpStr = args[0].split("\\s", 2);
        if (tmpStr.length != argNum) {
            throw new ShellException("Bad arguments");
        }
        if (tmpStr.length >= 2) {
            tmpStr[1] = tmpStr[1].trim();
            realArgs[1] = tmpStr[1];
        }
        if (tmpStr.length >= 1) {
            realArgs[0] = tmpStr[0];
        }
    }

    private static void printOut(Object toPrint) {
        System.out.println(toPrint);
    }

    public static ShellCommand[] getPackageCommands() {
        return new ShellCommand[]{
                new CreateCommand(),
                new DropCommand(),
                new UseCommand()
        };
    }

    static class CreateCommand implements ShellCommand {

        @Override
        public String getName() {
            return "create";
        }

        private List<Class<?>> getListTypes(String arg) throws ShellException, ColumnFormatException {
            if (arg.length() < 2) {
                throw new ShellException("Empty types list");
            } else {
                if (arg.charAt(0) != '(' && arg.charAt(arg.length() - 1) != ')') {
                    throw new ShellException("Strange types list");
                }
            }
            arg = arg.substring(1, arg.length() - 1);
            List<Class<?>> toReturn = new ArrayList<>();
            String[] types = arg.split("\\s");
            for (String type : types) {
                Class<?> currentType = StoreableUtils.getClassByString(type.trim());
                if (currentType == null) {
                    throw new ColumnFormatException("not supported type: " + type);
                }
                toReturn.add(currentType);
            }
            return toReturn;
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length < 1) {
                throw new ShellException(getName(), "Not enough arguments");
            }

            if (args.length > 1) {
                throw new ShellException(getName(), "Too many arguments");
            }

            String[] splitedArgs = args[0].split("\\s", 2);
            if (splitedArgs.length != 2) {
                throw new ShellException(getName(), "Bad arguments");
            }

            String tableName = splitedArgs[0].trim();
            if (tableName.length() == 0) {
                throw new ShellException(getName(), "No table name");
            }

            List<Class<?>> types;
            try {
                types = getListTypes(splitedArgs[1].trim());
            } catch (ColumnFormatException e) {
                System.out.println(String.format("wrong type ( %s )", e.getMessage()));
                return;
            }

            TableProvider provider = (TableProvider) info.getProperty("TableProvider");
            if (provider == null) {
                throw new ShellException(getName(), "Bad shell info: empty provider");
            }

            try {
                Table table = provider.createTable(tableName, types);
                if (table == null) {
                    printOut(tableName + " exists");
                } else {
                    printOut("created");
                }
            } catch (IllegalArgumentException e) {
                printOut(String.format("wrong type ( %s )", e.getMessage()));
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }
        }
    }

    static class DropCommand implements ShellCommand {

        @Override
        public String getName() {
            return "drop";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            String[] realArgs = new String[1];
            try {
                checkAndGetArguments(args, 1, realArgs);
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }

            TableProvider provider = (TableProvider) info.getProperty("TableProvider");
            if (provider == null) {
                throw new ShellException("Bad shell info: null provider");
            }

            String tableName = realArgs[0];

            try {
                Table table = provider.getTable(tableName);
                if (table == null) {
                    printOut(tableName + " not exists");
                } else {
                    provider.removeTable(realArgs[0]);
                    info.setProperty("CurrentTable", null);
                    printOut("dropped");
                }
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }
        }
    }

    static class UseCommand implements ShellCommand {

        @Override
        public String getName() {
            return "use";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            String[] realArgs = new String[1];
            try {
                checkAndGetArguments(args, 1, realArgs);
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }

            String tableName = realArgs[0];

            TableProvider provider = (TableProvider) info.getProperty("TableProvider");
            if (provider == null) {
                throw new ShellException("Bad shell info: null provider");
            }

            TableImplementation currentTable = (TableImplementation) info.getProperty("CurrentTable");
            if (currentTable != null) {
                int toCommitSize = currentTable.getUnsavedChangesCount();
                if (toCommitSize > 0) {
                    printOut(String.format("%d unsaved changes", toCommitSize));
                    return;
                }
            }
            try {
                TableImplementation toUseTable = (TableImplementation) provider.getTable(tableName);
                if (toUseTable == null) {
                    printOut(tableName + " not exists");
                } else {
                    info.setProperty("CurrentTable", toUseTable);
                    printOut("using " + tableName);
                }
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }
        }
    }
}
