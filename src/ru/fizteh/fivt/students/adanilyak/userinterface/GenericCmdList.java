package ru.fizteh.fivt.students.adanilyak.userinterface;

import ru.fizteh.fivt.students.adanilyak.commands.Cmd;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Alexander
 * Date: 20.10.13
 * Time: 22:27
 */
public class GenericCmdList {
    private static Map<String, Cmd> cmdlist = new HashMap<String, Cmd>();

    public GenericCmdList() {
        /**
         *  add static commands here
         *  (commands that you need in every shell)
         */
    }

    public Map<String, Cmd> getCmdList() {
        return cmdlist;
    }

    public void addCommand(Cmd command) {
        cmdlist.put(command.getName(), command);
    }
}
