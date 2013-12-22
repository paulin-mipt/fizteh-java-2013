package ru.fizteh.fivt.students.inaumov.shell;

import ru.fizteh.fivt.students.inaumov.shell.base.Shell;
import ru.fizteh.fivt.students.inaumov.shell.commands.*;

public class ShellMain {
    public static void main(String[] args) {
        Shell<ShellState> shell = new Shell<ShellState>();

        ShellState shellState = new ShellState();
        FileCommander fileCommander = new FileCommander();
        shellState.fileCommander = fileCommander;

        shell.setState(shellState);
        shell.setArgs(args);

        shell.addCommand(new CdCommand());
        shell.addCommand(new MkdirCommand());
        shell.addCommand(new PwdCommand());
        shell.addCommand(new RmCommand());
        shell.addCommand(new CpCommand());
        shell.addCommand(new MvCommand());
        shell.addCommand(new DirCommand());
        shell.addCommand(new ExitCommand());

        shell.run();
    }
}
