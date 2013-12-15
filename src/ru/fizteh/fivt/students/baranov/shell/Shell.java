package ru.fizteh.fivt.students.baranov.shell;

import java.util.HashMap;
import java.nio.file.Path;
import java.util.Scanner;

public class Shell {
    public HashMap<String, BasicCommand> commands;
    public ShellState path;

    Shell(Path pathC) {
        this.path = new ShellState(pathC);
        this.commands = new HashMap<String, BasicCommand>();
    }

    public void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        while (true) {
            if (!scanner.hasNextLine()) {
                return;
            }

            String commandString = scanner.nextLine().trim();
            if (commandString.equals("")) {
                System.out.print("$ ");
                continue;
            }
            String[] commandList = commandString.split("\\s*;\\s*");
            for (int i = 0; i < commandList.length; ++i) {
                String[] args = commandList[i].split("\\s+");
                BasicCommand command = commands.get(args[0]);
                if (command == null) {
                    System.err.println(args[0] + " - wrong command");
                    continue;
                }
                if (args[0].equals("exit")) {
                    return;
                }
                int x = command.doCommand(args, path);
                path.changeCurrentPath(path.getCurrentPath().normalize());
            }

            System.out.print("$ ");
            if (!scanner.hasNextLine()) {
                return;
            }
        }
    }

    public void pocketMode(String[] args) {
        String commandsString = "";
        for (int i = 0; i < args.length; ++i) {
            commandsString = commandsString + " " + args[i];
        }
        String[] commandList = commandsString.trim().split("\\s*;\\s*");
        for (int i = 0; i < commandList.length; ++i) {
            String[] arguments = commandList[i].split("\\s+");
            BasicCommand cmd = commands.get(arguments[0]);
            if (cmd == null) {
                System.err.println(arguments[0] + " - wrong command");
                System.exit(1);
            }
            if (arguments[0].equals("exit")) {
                return;
            }
            int answer = cmd.doCommand(arguments, path);
            // 0 - OK
            // 1 - error
            if (answer == 1) {
                System.exit(1);
            }
            path.changeCurrentPath(path.getCurrentPath().normalize());
        }
    }
}
