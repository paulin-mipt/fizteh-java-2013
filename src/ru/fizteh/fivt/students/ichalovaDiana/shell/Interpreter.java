package ru.fizteh.fivt.students.ichalovaDiana.shell;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Interpreter {

    private Map<String, Command> commands = new HashMap<String, Command>();

    public Interpreter(Map<String, Command> commands) {
        this.commands = commands;
    }

    public void run(String[] args) {
        if (args.length == 0) {
            runInteractiveMode();
        } else {
            runBatchMode(args);
        }
    }

    public void runInteractiveMode() {
        Scanner userInput = new Scanner(System.in);
        System.out.print("$ ");
        while (userInput.hasNextLine()) {
            String input = userInput.nextLine();
            try {
                executeCommands(input);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.print("$ ");
        }
        userInput.close();
    }

    public void runBatchMode(String[] args) {
        StringBuilder concatArgs = new StringBuilder();
        for (String item : args) {
            concatArgs.append(item).append(" ");
        }
        try {
            executeCommands(concatArgs.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void executeCommands(String input) throws Exception {
        String[][] inputCommandsWithParams = parseCommands(input);
        for (int i = 0; i < inputCommandsWithParams.length; ++i) {
            if (inputCommandsWithParams[i].length == 1
                    && inputCommandsWithParams[i][0].trim().equals("")) {
                continue;
            }
            Command cmd = commands.get(inputCommandsWithParams[i][0]);
            if (cmd == null) {
                throw new Exception("Command not found");
            }
            if (cmd.rawArgumentsNeeded || inputCommandsWithParams.length == 1) {
                cmd.execute(inputCommandsWithParams[i]);
            } else {
                String[] splitArguments = inputCommandsWithParams[i][1].split("\\s+");
                cmd.execute(concat(inputCommandsWithParams[i][0], splitArguments));
            }
             
        }
    }

    private String[][] parseCommands(String inputString) {
        String[] inputCommands = inputString.split("\\s*;\\s*");
        String[][] inputCommandsWithParams = new String[inputCommands.length][];
        for (int i = 0; i < inputCommands.length; ++i) {
            inputCommandsWithParams[i] = inputCommands[i].trim().split("\\s+", 2);
        }
        return inputCommandsWithParams;
    }
    
    private static String[] concat(String first, String[] second) {
        String[] result = new String[second.length + 1];
        result[0] = first;
        System.arraycopy(second, 0, result, 1, second.length);
        return result;
      }
}
