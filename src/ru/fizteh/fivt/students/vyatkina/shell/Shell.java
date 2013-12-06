package ru.fizteh.fivt.students.vyatkina.shell;

import ru.fizteh.fivt.students.vyatkina.Command;
import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.FileManager;
import ru.fizteh.fivt.students.vyatkina.State;
import ru.fizteh.fivt.students.vyatkina.TimeToFinishException;
import ru.fizteh.fivt.students.vyatkina.shell.commands.CdCommand;
import ru.fizteh.fivt.students.vyatkina.shell.commands.CpCommand;
import ru.fizteh.fivt.students.vyatkina.shell.commands.DirCommand;
import ru.fizteh.fivt.students.vyatkina.shell.commands.ExitCommand;
import ru.fizteh.fivt.students.vyatkina.shell.commands.MkdirCommand;
import ru.fizteh.fivt.students.vyatkina.shell.commands.MvCommand;
import ru.fizteh.fivt.students.vyatkina.shell.commands.PwdCommand;
import ru.fizteh.fivt.students.vyatkina.shell.commands.RmCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Shell implements ShellConstants {

    private State state;
    private Map<String, Command> COMMAND_MAP = new HashMap<>();
    private Mode mode;

    public enum Mode {
        PACKET,
        INTERACTIVE;
    }

    public Shell(Collection<Command> commands, Mode mode, State state) {
        this.state = state;
        for (Command c : commands) {
            COMMAND_MAP.put(c.getName(), c);
        }
        this.mode = mode;
    }

    public void startWork(String[] args) {
        switch (mode) {
            case INTERACTIVE: {
                startInteractiveMode();
                break;
            }
            case PACKET: {
                startPacketMode(concatenateArgs(args));
            }
        }
    }

    private void startPacketMode(String input) {
        try {
            for (CommandToExecute cmd : prepareArgs(input)) {
                cmd.execute();
            }
        }
        catch (IllegalStateException | IllegalArgumentException | CommandExecutionException e) {
            state.printErrorMessage(e.getMessage());
            Thread.currentThread().isInterrupted();
            throw new TimeToFinishException(TimeToFinishException.DEATH_MESSAGE);
        }
    }

    private void startInteractiveMode() {
        Scanner scanner = new Scanner(state.getIoStreams().in);
        while (!Thread.currentThread().isInterrupted()) {
            state.printInvitation();
            String line = scanner.nextLine();
            try {
                CommandToExecute cmd = parseCommandLine(line);
                cmd.execute();
            }
            catch (CommandExecutionException | IllegalArgumentException | IllegalStateException e) {
                state.printErrorMessage(e.getMessage());

            }
        }
        if (Thread.currentThread().isInterrupted()) {
            scanner.close();
        }
    }

    private class CommandToExecute {

        Command command;
        String[] args;

        CommandToExecute(Command command, String[] args) {
            this.command = command;
            this.args = args;
        }

        public void execute() {
            command.execute(args);
        }
    }

    private ArrayList<CommandToExecute> prepareArgs(String input) throws IllegalStateException {

        String[] commandsWithArgs = input.trim().split("\\s*;\\s*");
        ArrayList<CommandToExecute> commandsToExecute = new ArrayList<>();

        try {
            for (String commandLine : commandsWithArgs) {
                commandsToExecute.add(parseCommandLine(commandLine));
            }

        }
        catch (IllegalArgumentException | IllegalStateException e) {
            state.printErrorMessage(e.getMessage());
            throw new IllegalStateException(BAD_ARGUMENTS);
        }
        return commandsToExecute;

    }

    CommandToExecute parseCommandLine(String commandLine) {
        commandLine = commandLine.trim().replace("//s+", " ");
        String commandName;
        String commandSignature;
        if (commandLine.contains(" ")) {
            commandName = commandLine.substring(0, commandLine.indexOf(' '));
            commandSignature = commandLine.substring(commandLine.indexOf(' ') + 1);
        } else {
            commandName = commandLine;
            commandSignature = "";
        }

        if (!COMMAND_MAP.containsKey(commandName)) {
            throw new IllegalArgumentException(UNKNOWN_COMMAND + commandName);
        }

        Command command = COMMAND_MAP.get(commandName);
        String[] commandArgs = command.parseArgs(commandSignature);
        return new CommandToExecute(command, commandArgs);
    }

    public static void main(String[] args) {
        Shell shell;
        ShellState shellState = new ShellState(new FileManager());
        Set<Command> shellCommands = standardCommands(shellState);

        if (args.length == 0) {
            shell = new Shell(shellCommands, Mode.INTERACTIVE, shellState);
        } else {
            shell = new Shell(shellCommands, Mode.PACKET, shellState);
        }

        shell.startWork(args);
    }

    public static Set<Command> standardCommands(ShellState state) throws IllegalArgumentException {

        Set<Command> commands = new HashSet();
        commands.add(new DirCommand(state));
        commands.add(new PwdCommand(state));
        commands.add(new ExitCommand(state));
        commands.add(new CdCommand(state));
        commands.add(new MkdirCommand(state));
        commands.add(new CpCommand(state));
        commands.add(new RmCommand(state));
        commands.add(new MvCommand(state));

        return commands;
    }

    private String concatenateArgs(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg);
            sb.append(" ");
        }
        return sb.toString();
    }


}
