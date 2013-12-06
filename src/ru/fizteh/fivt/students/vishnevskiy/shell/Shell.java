package ru.fizteh.fivt.students.vishnevskiy.shell;

import java.util.Scanner;
import java.util.List;

public class Shell {
    private CommandsOperator commandsOperator;

    public Shell(List<Command> newCommands, State state) {
        commandsOperator = new CommandsOperator(newCommands, state);
    }

    private int executeLine(String line) {
        String[] commands = line.split(";");
        for (String command : commands) {
            int status = commandsOperator.runCommand(command);
            if (status == 1) {
                System.err.flush();
                return 1;
            }
        }
        return 0;
    }

    private void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            executeLine(line);
            System.out.print("$ ");
        }
    }

    private void batchMode(String[] args) {
        StringBuilder lineBuilder = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            lineBuilder.append(args[i]);
            lineBuilder.append(' ');
        }
        String line = lineBuilder.toString();
        int status = executeLine(line);
        System.exit(status);
    }

    public void run(String[] args) {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }

}
