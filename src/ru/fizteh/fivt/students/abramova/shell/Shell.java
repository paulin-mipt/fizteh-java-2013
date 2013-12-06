package ru.fizteh.fivt.students.abramova.shell;

import java.io.*;
import ru.fizteh.fivt.students.abramova.filemap.*;

public class Shell<Class> {
    private Status<Class> status;

    public Shell(Class object) {
        status = new Status<Class>(object);
    }

    public int doShell(String[][] commands) throws IOException {
        if (commands != null) {
            //Работа с переданными в Shell аргументами
             return commandLineMode(commands, false);
        }
        //Работа в интерактивном режиме
        return interactiveMode();
    }

    private int commandLineMode(String[][] commands, boolean inInteractive) throws IOException {
        int returnValue = 0;
        Command currentCommand;
        int commandNumber = 1;
        for (String commandName : commands[0]) {
            currentCommand = CommandGetter.getCommand(commandName, status);
            if (currentCommand != null) {
                if (currentCommand.correctArgs(commands[commandNumber])) {
                    returnValue = currentCommand.doCommand(commands[commandNumber], status);
                } else {
                    System.out.println(currentCommand.getName() + ": Wrong arguments");
                    returnValue = 3;
                }
                commandNumber++;
            } else {
                System.out.println("Wrong command \'" + commandName + "\'");
                returnValue = 4;
            }
            if (returnValue != 0 && !inInteractive) {
                break;
            }
        }
        if (!inInteractive) {
            if (status.isFileMap()) {
                status.getFileMap().close();
            }
            if (returnValue == -1) {
                returnValue = 0;
            }
        }
        return returnValue;
    }

    private int interactiveMode() throws IOException {
        int returnValue = 0;
        BufferedReader doIn = new BufferedReader(new InputStreamReader(System.in));
        String commandsWithArgs;
        String[][] commands;
        while (returnValue != -1) {
            printInvitation();
            try {
                commandsWithArgs = doIn.readLine();
            } catch (IOException e) {
                throw new IOException("Read error: " + e.getMessage());
            }
            if (commandsWithArgs != null && commandsWithArgs.length() != 0) {
                commands = Parser.parseArgs(commandsWithArgs.split(" "));
                returnValue = commandLineMode(commands, true);
            }
        }
        if (status.isFileMap()) {
            status.getFileMap().close();
        }
        return 0;
    }

    private void printInvitation() {
        String nowDir = "";
        if (status.isStage()) {
            nowDir = status.getStage().currentDirPath();
        }
        System.out.print(nowDir + " $ ");
    }
}
