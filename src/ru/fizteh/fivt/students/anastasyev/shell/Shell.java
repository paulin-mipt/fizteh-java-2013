package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.File;
import java.util.ArrayList;

public class Shell extends State {
    private static File userDir;
    private ArrayList<Command> commands;

    public Shell() {
        Shell.userDir = new File(System.getProperty("user.dir"));
        commands = new ArrayList<Command>();
        commands.add(new CdCommand());
        commands.add(new CpCommand());
        commands.add(new DirCommand());
        commands.add(new MkdirCommand());
        commands.add(new MvCommand());
        commands.add(new PwdCommand());
        commands.add(new RmCommand());
        commands.add(new ExitCommand());
    }

    public final void addCommand(final Command command) {
        commands.add(command);
    }

    public static File getUserDir() {
        return userDir;
    }

    public static void setUserDir(final File newUserDir) {
        userDir = newUserDir;
    }

    @Override
    public final ArrayList<Command> getCommands() {
        return commands;
    }
}
