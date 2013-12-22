package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Executor<State> {

    public Map<String, Command> mapOfCmd = new HashMap<String, Command>();

    public String[] argsCheck(String inCommand) {

        int openBracket = inCommand.indexOf('(');
        if (openBracket == -1) {
            int space = inCommand.indexOf(" ");
            if (-1 == space) {
                return new String[0];
            }
            String substr = inCommand.substring(space + 1);
            return substr.trim().split("\\ ");
        } else {
            String[] args = new String[2];
            int space = inCommand.indexOf(" ");
            String tmp = inCommand.substring(space + 1);
            space = tmp.indexOf(" ");
            args[0] = tmp.substring(0, space);
            args[1] = inCommand.substring(openBracket + 1, inCommand.indexOf(')'));
            return args;
        }
    }

    public String cmdCheck(String cmd) {

        String tmp;
        int space = cmd.indexOf(" ");
        if (-1 == space) {
            space = cmd.length();
        }
        tmp = cmd.substring(0, space);
        return tmp;
    }

    public abstract void list();

    public void execute(State state, String cmd) throws IOException {

        if (!mapOfCmd.containsKey(cmdCheck(cmd))) {
            throw new IOException("Can't find key");
        }
        mapOfCmd.get(cmdCheck(cmd)).executeCmd(state, argsCheck(cmd));
    }
}
