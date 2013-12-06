package ru.fizteh.fivt.students.anastasyev.shell;

import ru.fizteh.fivt.students.anastasyev.filemap.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Launcher {
    private ArrayList<Command> allCommands;
    private State state;

    /*private void beforeStop() {
        try {
            state.stopping();
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
        }
    }*/

    private boolean launch(final String arg) throws IOException {
        if (arg.equals("")) {
            return true;
        }
        String[] commands = arg.split("\\s+", 3);
        boolean result = false;
        int i = 0;
        for (; i < allCommands.size(); ++i) {
            if (allCommands.get(i).commandName().equals(commands[0])) {
                result = allCommands.get(i).exec(state, commands);
                break;
            }
        }
        if (i >= allCommands.size()) {
            System.err.println("Wrong command " + arg);
            return false;
        }
        return result;
    }

    public void interactiveMode() throws ExitException {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.err.flush();
            System.out.print("$ ");
            try {
                String commands = scan.nextLine().trim();
                String[] allArgs = commands.split(";");
                for (String arg : allArgs) {
                    if (!arg.equals("")) {
                        if (!launch(arg.trim())) {
                            break;
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                //beforeStop();
                System.exit(1);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void packageMode(final String[] args) throws ExitException {
        StringBuilder packageCommandsNames = new StringBuilder();
        for (String arg : args) {
            packageCommandsNames.append(arg).append(" ");
        }
        String commands = packageCommandsNames.toString();
        String[] allArgs = commands.split(";");
        try {
            for (String arg : allArgs) {
                if (!launch(arg.trim())) {
                    throw new ExitException();
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new ExitException();
        }
    }

    public Launcher(State newState, String[] args) {
        state = newState;
        allCommands = state.getCommands();
        int exitCode = 0;
        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                packageMode(args);
            }
        } catch (ExitException e) {
            exitCode = 1;
        } finally {
            //beforeStop();
            System.exit(exitCode);
        }
    }
}

