package ru.fizteh.fivt.students.asaitgalin.shell;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class CommandTable {
    private Map<String, Command> table = new HashMap<String, Command>();

    public void appendCommand(Command cmd) {
        table.put(cmd.getName(), cmd);
    }

    public void executeCommandLine(String commandsLine) throws IOException {
        String[] commands = commandsLine.trim().split("\\s*;\\s*");
        for (String s: commands) {
            String commandName = s.split("\\s+", 2)[0];
            Command cmd = table.get(commandName);
            if (cmd != null) {
                String[] cmdArgs = cmd.parseCommandLine(s);
                if (cmdArgs == null) {
                    throw new IOException(cmd.getName() + ": error while parsing command");
                }
                if (cmdArgs.length - 1 != cmd.getArgsCount()) {
                    throw new IOException(cmd.getName() + ": wrong argument count");
                }
                cmd.execute(cmdArgs);
            } else {
                throw new IOException(commandName + ": unrecognized command");
            }
        }
    }

}
