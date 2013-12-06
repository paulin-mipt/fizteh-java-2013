package ru.fizteh.fivt.students.vishnevskiy.shell;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

import ru.fizteh.fivt.students.vishnevskiy.shell.commands.*;

public class CommandsOperator {
    private Map<String, Command> commandsTable = new HashMap<String, Command>();
    private State state;

    private void loadShellCommands() {
        Cd cd = new Cd();
        commandsTable.put(cd.getName(), cd);
        Cp cp = new Cp();
        commandsTable.put(cp.getName(), cp);
        Dir dir = new Dir();
        commandsTable.put(dir.getName(), dir);
        Exit exit = new Exit();
        commandsTable.put(exit.getName(), exit);
        MkDir mkdir = new MkDir();
        commandsTable.put(mkdir.getName(), mkdir);
        Mv mv = new Mv();
        commandsTable.put(mv.getName(), mv);
        Pwd pwd = new Pwd();
        commandsTable.put(pwd.getName(), pwd);
        Rm rm = new Rm();
        commandsTable.put(rm.getName(), rm);
    }

    private void loadNewCommands(List<Command> newCommands) {
        for (Command command : newCommands) {
            commandsTable.remove(command.getName());
            commandsTable.put(command.getName(), command);
        }
    }


    public CommandsOperator(List<Command> newCommands, State state) {
        loadShellCommands();
        loadNewCommands(newCommands);
        this.state = state;
    }

    public int runCommand(String line) {
        try {
            line = line.trim();
            String commandName = line.split("\\s", 2)[0];
            Command command = commandsTable.get(commandName);
            if (command == null) {
                if (commandName.equals("")) {
                    return 0;
                } else {
                    throw new CommandException(commandName + ": command not found");
                }
            }
            String[] args = line.split("\\s+", (command.getArgsNum() == 0) ? 2 : command.getArgsNum() + 1);
            int i = 1;
            while ((i < args.length) && (!args[i].isEmpty())) {
                ++i;
            }
            args = Arrays.copyOfRange(args, 1, i);
            command.execute(state, args);
        } catch (CommandException e) {
            System.err.println(e.getMessage());
            return 1;
        }
        return 0;
    }
}
