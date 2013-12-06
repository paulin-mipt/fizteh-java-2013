package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.filemap.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shell {
    public State curState;
    private HashMap<String, Command> cmds = new HashMap<String, Command>();

    public Shell(State state) {
        curState = state;
    }

    public void fillHashMap(ArrayList<Command> cmdList) {
        for (Command itCmd : cmdList) {
            cmds.put(itCmd.getCmd(), itCmd);
        }
    }

    String getCommandName(String command) {
        command = command.trim();
        return (command.split("\\s+"))[0];
    }

    String[] getParams(String command, boolean flag) {
        command = command.trim();
        int spaceEntry = command.indexOf(' ');
        String[] res = {};
        if (spaceEntry == -1) {
            String[] result = {};
            return result;
        } else {
            if (!flag) {
                res = command.substring(spaceEntry + 1).trim().split("\\s+");
                for (int i = 0; i < res.length; i++) {
                    res[i] = res[i].trim();
                }
                return res;
            } else {
                String[] answer = command.split("[\\s]+", 3);
                if (answer.length == 3) {
                    String[] result = {answer[1], answer[2]};
                    return result;
                } else {
                    String[] result = {answer[1]};
                    return result;
                }

            }
        }
    }

    boolean processCommand(String command) {
        if (command.length() == 0) {
            return true;
        }
        Pattern p = Pattern.compile("\\s+");
        Matcher m = p.matcher(command);
        if (m.matches()) {
            return true;
        }
        String commandName = getCommandName(command);
        if (!cmds.containsKey(commandName)) {
            System.err.println("Invalid input");
            return false;
        }
        boolean flag = false;
        boolean flagCreate = false;
        if ((cmds.get(commandName).getCmd().equals("put")) || (cmds.get(commandName).getCmd().equals("create"))) {
            flag = true;
        }
        String[] params = getParams(command, flag);
        try {
            if (cmds.get(commandName).getCmd().equals("exit")) {
                System.exit(0);
            }
            if (!cmds.get(commandName).exec(params, curState)) {
                return false;
            }

        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error with input/output");
            return false;
        }
        return true;
    }

    public void interActive() {
        Scanner scan = new Scanner(System.in);
        System.out.print(" $ ");
        try {
            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                String[] commandArray = input.split(";");
                for (final String command : commandArray) {
                    try {
                        processCommand(command);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
                System.out.print(" $ ");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + e.getCause());
            e.printStackTrace();
            String[] ar = {};
            try {
                cmds.get("exit").exec(ar, curState);
                System.exit(0);
            } catch (IOException r) {
                System.exit(1);
            } catch (IllegalArgumentException f) {
                System.err.println(f.getMessage());
            }
            System.exit(1);
        }
        String[] ar = {};
        try {
            cmds.get("exit").exec(ar, curState);
            System.exit(0);
        } catch (IOException e) {
            System.exit(1);
        }
        scan.close();
    }

    public void pocket(String[] args) {
        StringBuilder expressionBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            expressionBuilder.append(args[i]);
            expressionBuilder.append(" ");
        }
        String expression = expressionBuilder.toString();
        String[] commandArray = expression.split(";");
        for (final String command : commandArray) {
            if (!processCommand(command)) {
                String[] ar = {};
                try {
                    cmds.get("exit").exec(ar, curState);
                    System.exit(1);
                } catch (IOException e) {
                    System.exit(1);
                }
                System.exit(1);
            }
        }
    }
}

