package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;

import java.io.File;
import java.io.IOException;

public class ShellMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        File curDir = new File("");
        curDir = curDir.getCanonicalFile();
        try {
            AbstractFrame<ShellState> shell = new AbstractShell(curDir);

            if (args.length != 0) {
                shell.batchMode(args);
            } else {
                shell.interactiveMode();
            }
        } catch (Exception e) {
            System.err.println("ERROR: directory problem\n");
            System.exit(1);
        }

        System.exit(0);
    }
}
