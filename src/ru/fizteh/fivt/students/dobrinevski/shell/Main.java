package ru.fizteh.fivt.students.dobrinevski.shell;

import java.io.File;
import java.util.HashMap;

public class Main {
    private static HashMap<String, Command> cmdMap = new HashMap<String, Command>();

    static {
        cmdMap.put("cd", new ShellCommands.Cd());
        cmdMap.put("mkdir", new ShellCommands.Mkdir());
        cmdMap.put("pwd", new ShellCommands.Pwd());
        cmdMap.put("rm", new ShellCommands.Remove());
        cmdMap.put("cp", new ShellCommands.Copy());
        cmdMap.put("mv", new ShellCommands.Move());
        cmdMap.put("dir", new ShellCommands.Dir());
        cmdMap.put("exit", new ShellCommands.Exit());
    }

    public static void main(String[] args) throws Exception {
        String way = System.getProperty("user.dir");
        if (way == null) {
            throw new Exception("Illegal table");
        }
        File dbsDir = new File(way);
        if (!dbsDir.isDirectory()) {
            throw new Exception(dbsDir + " doesn't exist or is not a directory");
        }

        Shell sl = new Shell(cmdMap, "user.dir");
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg).append(' ');
            }
            try {
                sl.executeCommands(builder.toString());
            } catch (Exception e) {
                System.err.println(e);
                System.exit(1);
            }
        } else {
            sl.iMode();
        }
    }
}
