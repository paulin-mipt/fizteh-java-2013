package ru.fizteh.fivt.students.abramova.shell;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main (String[] args) {
        int exitValue = 0;
        String[][] argumentsForShell = null;
        if (args.length != 0) {
            argumentsForShell = Parser.parseArgs(args);
        }
        try {
            exitValue = new Shell(new Stage(new File("").getAbsolutePath())).doShell(argumentsForShell);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        System.exit(exitValue);
    }
}
