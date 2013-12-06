package ru.fizteh.fivt.students.musin.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Shell {

    private HashMap<String, ShellCommand> commands;
    public File currentDirectory;
    boolean exit;

    public void stop() {
        exit = true;
    }

    public Shell(String startDirectory) {
        currentDirectory = new File(startDirectory);
        commands = new HashMap<String, ShellCommand>();
        commands.put("exit", new ShellCommand("exit", new ShellExecutable() {
            @Override
            public int execute(Shell shell, ArrayList<String> args) {
                stop();
                return 0;
            }
        }));
    }

    private int parseString(String s) {
        String[] comm = s.split(";");
        for (int i = 0; i < comm.length; i++) {
            String[] tokens = comm[i].split("\\s+");
            ArrayList<String> args = new ArrayList<>();
            ArrayList<String> selfParseArgs = new ArrayList<>();
            String name = "";
            for (int j = 0; j < tokens.length; j++) {
                if (!tokens[j].equals("")) {
                    if (name.equals("")) {
                        name = tokens[j];
                    } else {
                        args.add(tokens[j]);
                    }
                }
            }
            for (int j = 1; j < comm[i].length(); j++) {
                if (Character.isWhitespace(comm[i].charAt(j)) && !Character.isWhitespace(comm[i].charAt(j - 1))) {
                    selfParseArgs.add(comm[i].substring(j, comm[i].length()).trim());
                    break;
                }
            }
            if (selfParseArgs.size() == 0) {
                selfParseArgs.add("");
            }
            ShellCommand command = commands.get(name);
            if (command != null) {
                if (command.parsingRequired) {
                    if (command.exec.execute(this, args) != 0) {
                        return -1;
                    }
                } else {
                    if (command.exec.execute(this, selfParseArgs) != 0) {
                        return -1;
                    }
                }
            }
            if (command == null && !name.equals("")) {
                System.err.printf("No such command %s\n", name);
                return -1;
            }
        }
        return 0;
    }

    public void addCommand(ShellCommand command) {
        commands.put(command.name, command);
    }

    public int runArgs(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        String argString = sb.toString();
        return parseString(argString);
    }

    public int run(BufferedReader br) {
        exit = false;
        while (!exit) {
            System.out.print(" $ ");
            try {
                String str = br.readLine();
                if (str == null) {
                    return 0;
                }
                parseString(str);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return 0;
    }

    public interface ShellExecutable {
        int execute(Shell shell, ArrayList<String> args);
    }

    public static class ShellCommand {
        String name;
        ShellExecutable exec;
        boolean parsingRequired;

        public ShellCommand(String name, ShellExecutable exec) {
            this.name = name;
            this.exec = exec;
            this.parsingRequired = true;
        }

        public ShellCommand(String name, boolean parsingRequired, ShellExecutable exec) {
            this.name = name;
            this.exec = exec;
            this.parsingRequired = parsingRequired;
        }
    }
}
