package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.util.Hashtable;

public class CommandFactory {
    Hashtable<String, Command> commands;
    public CommandFactory(Hashtable<String, Command> commandsList) {
        commands = commandsList;
    }

    public Command makeCommand(String name) throws Exception {
        Command cmd = commands.get(name);
        if (cmd == null) {
            throw new IllegalArgumentException("command not found");
        } else {
            return cmd;
        }
    }
}
