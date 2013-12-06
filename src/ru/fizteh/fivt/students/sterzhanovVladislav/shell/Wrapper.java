package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

import java.util.HashMap;

public class Wrapper {
    private static HashMap<String, Command> cmdMap = new HashMap<String, Command>();
    
    static {
        cmdMap.put("cd", new ShellCommands.Cd());
        cmdMap.put("mkdir", new ShellCommands.Mkdir());
        cmdMap.put("pwd", new ShellCommands.Pwd());
        cmdMap.put("rm", new ShellCommands.Rm());
        cmdMap.put("cp", new ShellCommands.Cp());
        cmdMap.put("mv", new ShellCommands.Mv());
        cmdMap.put("dir", new ShellCommands.Dir());
        cmdMap.put("exit", new ShellCommands.Exit());
    }
    
    public static void main(String[] args) {
        ShellUtility.execShell(args, cmdMap);
        System.exit(0);
    }
}
