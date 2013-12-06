package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.util.Hashtable;
import java.io.File;

public class Shell {
    static Hashtable<String, Command> commandsMap = new Hashtable<String, Command>();

    public static File path;

    public static File getPath() {
        return path;
    }

    public static void setPath(File path) {
        Shell.path = path;
    }

    public static String getFullPath(String smallPath) {
        File myFile = new File(smallPath);
        if (myFile.isAbsolute()) {
            return smallPath;
        } else {
            return path.getAbsolutePath() + File.separator + smallPath;
        }
    }

    public static void addCommands() {
        commandsMap.put("cd", new CdCommand());
        commandsMap.put("mkdir", new MkdirCommand());
        commandsMap.put("pwd", new PwdCommand());
        commandsMap.put("rm", new RmCommand());
        commandsMap.put("cp", new CpCommand());
        commandsMap.put("mv", new MvCommand());
        commandsMap.put("dir", new DirCommand());
        commandsMap.put("exit", new ExitCommand());
    }
    public static void main(String[] args) {
        path = new File(".");
        addCommands();
        CommandRunner runner = new CommandRunner(path, commandsMap);
        runner.run(args);
    }

}
