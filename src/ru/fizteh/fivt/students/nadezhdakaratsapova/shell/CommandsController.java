package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandsController {
    private Map<String, Command> commandsStorage = new HashMap<String, Command>();

    public void addCmd(Command cmd) {
        commandsStorage.put(cmd.getName(), cmd);
    }

    public void runCommand(String[] command) throws IOException {
        if (!(command[0].length() == 0)) {
            Command cmd = commandsStorage.get(command[0]);
            if (cmd == null) {
                throw new IOException(command[0] + ": unknown command");
            } else {
                if (!cmd.compareArgsCount(command.length - 1)) {
                    throw new IOException(cmd.getName() + ": wrong number of arguments.");
                } else {
                    cmd.execute(command);
                }
            }
        }
    }


}
