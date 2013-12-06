package ru.fizteh.fivt.students.dmitryKonturov.dataBase.shellEnvironment;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellCommand;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellInfo;

public class WorkWithChosenTableCommands {


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
        System.out.flush();
    }

    public static ShellCommand[] getPackageCommands() {
        return new ShellCommand[] {
                new PutCommand(),
                new GetCommand(),
                new RemoveCommand(),
                new SizeCommand(),
                new CommitCommand(),
                new RollbackCommand()
        };
    }

    static class PutCommand implements ShellCommand {

        @Override
        public String getName() {
            return ("put");
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            String[] realArgs = new String[2];
            try {
                checkAndGetArguments(args, 2, realArgs);
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }

            TableProvider provider = (TableProvider) info.getProperty("TableProvider");
            if (provider == null) {
                throw new ShellException("Bad shell info: null provider");
            }
            Table table = (Table) info.getProperty("CurrentTable");
            if (table == null) {
                printOut("no table");
                return;
            }
            try {
                Storeable value = provider.deserialize(table, realArgs[1]);
                Storeable oldValue = table.put(realArgs[0], value);
                if (oldValue == null) {
                    printOut("new");
                } else {
                    printOut("overwrite");
                    String toWrite = provider.serialize(table, oldValue);
                    printOut(toWrite);
                }
            } catch (Exception e) {
                printOut(String.format("wrong type ( %s )", ShellEmulator.getNiceMessage(e)));
            }
        }
    }

    static class GetCommand implements ShellCommand {

        @Override
        public String getName() {
            return "get";
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
            Table table = (Table) info.getProperty("CurrentTable");
            if (table == null) {
                printOut("no table");
                return;
            }
            try {
                Storeable value = table.get(realArgs[0]);
                if (value == null) {
                    printOut("not found");
                } else {
                    String answer = provider.serialize(table, value);
                    printOut("found");
                    printOut(answer);
                    // костыль для cradle
                    int f = 0;
                    while (f < 1000000) {
                        ++f;
                    }
                    //end костыль
                }
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }
        }
    }

    static class RemoveCommand implements ShellCommand {

        @Override
        public String getName() {
            return "remove";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            String[] realArgs = new String[1];
            try {
                checkAndGetArguments(args, 1, realArgs);
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }

            Table table = (Table) info.getProperty("CurrentTable");
            if (table == null) {
                printOut("no table");
                return;
            }
            try {
                Storeable value = table.remove(realArgs[0]);

                if (value == null) {
                    printOut("not found");
                } else {
                    printOut("removed");
                }
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }
        }
    }

    static class SizeCommand implements ShellCommand {

        @Override
        public String getName() {
            return "size";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length > 0) {
                throw new ShellException("Too many arguments");
            }

            Table table = (Table) info.getProperty("CurrentTable");
            if (table == null) {
                printOut("no table");
                return;
            }
            try {
                printOut(table.size());
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }
        }
    }

    static class CommitCommand implements ShellCommand {

        @Override
        public String getName() {
            return "commit";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length > 0) {
                throw new ShellException("Too many arguments");
            }

            Table table = (Table) info.getProperty("CurrentTable");
            if (table == null) {
                printOut("no table");
                return;
            }
            try {
                printOut(table.commit());
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }
        }
    }

    static class RollbackCommand implements ShellCommand {

        @Override
        public String getName() {
            return "rollback";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length > 0) {
                throw new ShellException("Too many arguments");
            }

            Table table = (Table) info.getProperty("CurrentTable");
            if (table == null) {
                printOut("no table");
                return;
            }
            try {
                printOut(table.rollback());
            } catch (Exception e) {
                throw new ShellException(getName(), e);
            }
        }
    }
}
