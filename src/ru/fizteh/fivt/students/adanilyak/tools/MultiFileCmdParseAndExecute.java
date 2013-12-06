package ru.fizteh.fivt.students.adanilyak.tools;

import ru.fizteh.fivt.students.adanilyak.commands.Cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * User: Alexander
 * Date: 20.10.13
 * Time: 22:38
 */
public class MultiFileCmdParseAndExecute {
    public static List<String> intoCommandsAndArgs(String cmd, String delimetr) {
        cmd.trim();
        String[] tokens = cmd.split(delimetr);
        List<String> result = new ArrayList();
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("") && !tokens[i].matches("\\s+")) {
                result.add(tokens[i]);
            }
        }
        return result;
    }

    public static void execute(String cmdWithArgs, Map<String, Cmd> cmdList) throws IOException {
        List<String> cmdAndArgs = intoCommandsAndArgs(cmdWithArgs, " ");
        try {
            String commandName = cmdAndArgs.get(0);
            if (!cmdList.containsKey(commandName)) {
                throw new NoSuchElementException("Unknown command");
            }

            Cmd command = cmdList.get(commandName);
            if (cmdAndArgs.size() != command.getAmArgs() + 1) {
                throw new IOException("Wrong amount of arguments");
            }

            command.work(cmdAndArgs);
        } catch (IOException exc) {
            System.err.println(cmdAndArgs + ": " + exc.getMessage());
        }
    }
}
