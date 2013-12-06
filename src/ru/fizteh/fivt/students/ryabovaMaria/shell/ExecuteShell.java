package ru.fizteh.fivt.students.ryabovaMaria.shell;

import java.io.File;

public class ExecuteShell {
    private static ShellCommands commands;
    private static Shell shell;
    
    public static void main(String[] args) {
        int argc = args.length;
        String getPropertyString = System.getProperty("user.dir");
        if (getPropertyString == null) {
            System.err.println("I can't find this directory");
            System.exit(1);
        }
        commands = new ShellCommands();
        commands.currentDir = new File(getPropertyString);
        shell = new Shell(commands);
        if (argc == 0) {
            shell.interactive();
        } else {
            shell.packet(args);
        }
    }
}
