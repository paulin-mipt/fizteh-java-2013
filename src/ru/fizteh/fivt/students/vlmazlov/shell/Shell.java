package ru.fizteh.fivt.students.vlmazlov.shell;

import java.util.*;

public class Shell<T> {

    private Map<String, Command<T>> supportedCommands;
    private static final String INVITATION = "$ ";
    private T state;

    public Shell(Command<T>[] commands, T state) {

        Map<String, Command<T>> supportedCommands = new TreeMap<String, Command<T>>();

        for (Command<T> command : commands) {
            supportedCommands.put(command.getName(), command);
        }

        this.supportedCommands = Collections.unmodifiableMap(supportedCommands);

        this.state = state;
    }


    public static void main(String[] args) {
        ShellState state = new ShellState(System.getProperty("user.dir"));


        Command[] commands = {
                new RmCommand(), new CdCommand(),
                new MvCommand(), new MkdirCommand(), new CpCommand(),
                new PwdCommand(), new DirCommand(), new ExitCommand<ShellState>()
        };

        Shell<ShellState> shell = new Shell<ShellState>(commands, state);

        try {
            shell.process(args);
        } catch (WrongCommandException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (CommandFailException ex) {
            System.err.println("error while processing command: " + ex.getMessage());
            System.exit(2);
        } catch (UserInterruptionException ex) {
            System.exit(0);
        }

        System.exit(0);
    }

    public void process(String[] args)
            throws WrongCommandException, CommandFailException, UserInterruptionException {

        if (0 != args.length) {

            String arg = StringUtils.join(Arrays.asList(args), " ");

            executeLine(arg);
        } else {
            interactiveMode();
        }
    }

    private String[] parseLine(String commandLine) {
        commandLine = commandLine.trim();
        return commandLine.split("(\\s*;\\s*)", -1);
    }

    private void executeLine(String commandLine)
            throws WrongCommandException, CommandFailException, UserInterruptionException {

        for (String exArg : parseLine(commandLine)) {
            invokeCommand(exArg.split("\\s+(?![^\\(]*\\))"));
        }
    }

    private void interactiveMode() {
        Scanner inputScanner = new Scanner(System.in);
        Scanner stringScanner;

        do {
            System.out.print(INVITATION);

            try {
                executeLine(inputScanner.nextLine());
            } catch (WrongCommandException ex) {

                System.err.println(ex.getMessage());
            } catch (CommandFailException ex) {

                System.err.println(ex.getMessage());
            } catch (UserInterruptionException ex) {

                return;
            }

        } while (!Thread.currentThread().isInterrupted());
    }

    private void invokeCommand(String[] toExecute)
            throws WrongCommandException, CommandFailException, UserInterruptionException {
        //toExecute[0] should be the beginning of the command
        if (0 == toExecute.length) {
            throw new WrongCommandException("Empty command");
        }

        if (toExecute[0].matches("\\s*")) {
            throw new WrongCommandException("Syntax error near unexpected token ;");
        }

        Command invokedCommand = supportedCommands.get(toExecute[0]);

        if (null == invokedCommand) {
            throw new WrongCommandException("Unknown command: " + toExecute[0]);
        } else if ((toExecute.length - 1) != invokedCommand.getArgNum()) {
            throw new WrongCommandException("Ivalid number of arguments for " 
                + toExecute[0] + ": " + (toExecute.length - 1));
        }

        invokedCommand.execute(Arrays.copyOfRange(toExecute, 1, toExecute.length), state, System.out);
    }
}

