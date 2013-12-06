package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
public class CommandsMap {
    private Map<String, Commands> commands = new HashMap<String, Commands>();

    public void addCommand(Commands command) {
        if (!commands.containsKey(command.getName())) {
            commands.put(command.getName(), command);
        }
    }


    public void execute(String commandsString) throws IOException {
        String[] commandsArray = commandsString.trim().split("\\s*;\\s*");
        for (String command : commandsArray) {
            String[] args = command.split("\\s+");
            if (commands.containsKey(args[0])) {
                commands.get(args[0]).perform(args);
            } else {
                throw new IOException("No command with such name: " + args[0]);
            }
        }
    }


    public void removeCommand(String commandName) throws IOException {
        if (commands.containsKey(commandName)) {
            commands.remove(commandName);
        } else {
            throw new IOException("no command with name: " + commandName);
        }
    }

}
