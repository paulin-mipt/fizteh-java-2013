package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

import java.util.*;

public class MultiFileHashMap {

    static TableProvider provider;
    static Table currentTable;
    static Map<String, Command> commandList;


    public static boolean batchMode(String input) throws TimeToExitException {

        String[] commands = input.split("\\s*;\\s*");

        for (String cmd : commands) {
            cmd = cmd.trim(); //сомневаюсь, нужно ли это
            if (cmd.isEmpty()) {
                continue;
            }

            String cmdName;
            String[] cmdArgs;
            if (cmd.contains(" ")) {
                cmdName = cmd.substring(0, cmd.indexOf(" "));
                cmdArgs = cmd.substring(cmd.indexOf(" ") + 1, cmd.length())
                        .trim().split("\\s+");
                for (String cmdArg : cmdArgs) {
                    cmdArg = cmdArg.trim();
                }
            } else {
                cmdName = cmd;
                cmdArgs = new String[0];
            }

            if (cmdName.equals("exit")) {
                throw new TimeToExitException();
            }

            Command command = commandList.get(cmdName);
            if (command != null) {
                if (command.getArgsCount() != cmdArgs.length) {
                    System.err.println(command.getName() + ": Wrong count of arguments!");
                    return false;
                }
                try {
                    command.execute(cmdArgs);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            } else {
                System.err.println("Unknown command: " + cmdName);
                return false;
            }
        }

        return true;
    }


    public static void interactiveMode() throws TimeToExitException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();

            batchMode(input);
        }
    }


    public static void main(String[] args) {

        commandList = new TreeMap<String, Command>();

        commandList.put("get", new CommandGet());
        commandList.put("put", new CommandPut());
        commandList.put("remove", new CommandRemove());

        commandList.put("create", new CommandCreate());
        commandList.put("drop", new CommandDrop());
        commandList.put("use", new CommandUse());

        commandList.put("size", new CommandSize());
        commandList.put("commit", new CommandCommit());
        commandList.put("rollback", new CommandRollback());

        TableProviderFactory factory = new TableProviderFactory();

        try {
            provider = factory.create(System.getProperty("fizteh.db.dir"));
        } catch (Throwable e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                StringBuilder sb = new StringBuilder();
                for (String arg : args) {
                    sb.append(arg).append(" ");
                }
                batchMode(sb.toString());
            }
        } catch (TimeToExitException e) {
            System.exit(0);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            System.exit(1);
        }

        System.exit(1);

    }

}
