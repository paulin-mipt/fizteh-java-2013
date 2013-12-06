package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.File;
import java.io.IOException;

public class ShellMain {

    public static void main(String[] args) throws IOException {

        File currentDirectory = new File("");
        ShellState startState = new ShellState(currentDirectory);
        Shell<ShellState> shell = new Shell<ShellState>(startState);
        ShellExecutor exec = new ShellExecutor();
        if (args.length != 0) {
            shell.batchState(args, exec);
        } else {
            shell.interactiveState(exec);
        }
        System.exit(0);
    }
}