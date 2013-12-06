package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Shell {
    private Map<String, Command> commands = new HashMap<String, Command>();
    public static final String INCORRECT_INPUT = "Incorrect input";
    public static final String INCORRECT_NUMBER_OF_ARGUMENTS = "Incorrect number of arguments";
    State state;

    protected void checkInput(String[] args) {
        if (args.length == 0) {
            state.isInteractive = true;
        } else {
            state.isInteractive = false;
        }
    }

    public static Set<Command> shellCommands(State state) {
        Set<Command> shellCommands = new HashSet<Command>();
        shellCommands.add(new CommandCd(state));
        shellCommands.add(new CommandCp(state));
        shellCommands.add(new CommandDir(state));
        shellCommands.add(new CommandMkdir(state));
        shellCommands.add(new CommandPwd(state));
        shellCommands.add(new CommandRm(state));
        shellCommands.add(new CommandMv(state));
        shellCommands.add(new CommandExit(state));
        return shellCommands;

    }

    public Shell(State stateNew, Set<Command> commands) {
        for (Command cmd : commands) {
            this.commands.put(cmd.getName(), cmd);
        }
        state = stateNew;
    }

    protected String[] extractArgumentsFromInputString(String input) {
        return input.split("[ ]+", 3);
    }

    protected String makeNewInputString(String[] str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length; ++i) {
            result.append(str[i]);
            result.append(" ");
        }
        return result.toString();
    }

    protected void doPackageMode(String[] input) {
        String newInput = makeNewInputString(input);
        Scanner scanner = new Scanner(newInput);
        scanner.useDelimiter("[ ]*;[ ]*");
        while (scanner.hasNext()) {
            String current = scanner.next();
            current = rewriteInput(current);
            if (!current.isEmpty()) {
                doCommand(extractArgumentsFromInputString(current));
            } else {
                printError(INCORRECT_INPUT);
            }
        }
        scanner.close();
    }

    private void doCommand(String[] cmd) {
        String currentCmd = cmd[0];
        if (commands.containsKey(currentCmd)) {
            if (commands.get(currentCmd).numberOfArguments() == (cmd.length - 1)) {
                commands.get(currentCmd).executeProcess(cmd);
            } else {
                printError(INCORRECT_NUMBER_OF_ARGUMENTS);
            }
        } else {
            printError(INCORRECT_INPUT);
        }

    }

    protected void printError(String s) {
        if (state.isInteractive) {
            System.out.println(s);
        } else {
            System.err.println(s);
            System.exit(1);
        }
    }

    private String rewriteInput(String current) {
        return current.trim();
    }

    private void parseString() {
        String cur;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            cur = scanner.nextLine();
            cur = rewriteInput(cur);
            Scanner scanner1 = new Scanner(cur);
            scanner1.useDelimiter("[ ]*;[ ]*");
            while (scanner1.hasNext()) {
                String current = scanner1.next();
                current = rewriteInput(current);
                if (!current.isEmpty()) {
                    doCommand(extractArgumentsFromInputString(current));
                }
            }
            System.out.print("$ ");
            scanner1.close();
        }
        scanner.close();
    }

    protected void doInteractiveMode() {
        System.out.print(" $ ");
        parseString();
    }

    public static void main(String[] args) {
        State state = new State(new File(System.getProperty("user.dir")));
        Shell sh = new Shell(state, shellCommands(state));
        sh.startWork(args);
    }

    public void startWork(String[] args) {
        checkInput(args);
        if (state.isInteractive) {
            doInteractiveMode();
        } else {
            doPackageMode(args);
        }

    }
}
