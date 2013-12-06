// Kochetov Nicolai, 294, Shell

package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.IOException;
import java.util.HashMap;

public class Shell {

    private HashMap<String, Executable> commands;

    private Shell(FileManager manager) {
        commands = new HashMap<>();
        commands.put("cd", new CdCommand(manager));
        commands.put("cp", new CpCommand(manager));
        commands.put("dir", new DirCommand(manager));
        commands.put("exit", new ExitCommand(manager));
        commands.put("mkdir", new MkdirCommand(manager));
        commands.put("mv", new MvCommand(manager));
        commands.put("pwd", new PwdCommand(manager));
        commands.put("rm", new RmCommand(manager));
    }

    public static void main(String[] args) throws IOException {
        FileManager manager = new FileManager();
        Shell shell = new Shell(manager);
        Launcher launcher = new Launcher(shell.commands, new StringParser() {
            @Override
            public String[] parse(String string) {
                return string.trim().split("[\\s]+");
            }
        });
        try {
            if (!launcher.launch(args, manager)) {
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
