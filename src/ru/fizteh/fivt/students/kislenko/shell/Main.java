package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File startingDirectory = new File("");
        startingDirectory = startingDirectory.getAbsoluteFile();
        ShellState starting = new ShellState();
        starting.setState(startingDirectory.toPath());
        Command[] commandList = new Command[]{new CommandCd(), new CommandCp(), new CommandDir(),
                new CommandMkdir(), new CommandMv(), new CommandPwd(), new CommandRm()};
        Shell<ShellState> shell = new Shell<ShellState>(starting, commandList);
        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            shell.batchMode(args);
        }
        System.exit(0);
    }
}
