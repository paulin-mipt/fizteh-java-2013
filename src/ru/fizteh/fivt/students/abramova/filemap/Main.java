package ru.fizteh.fivt.students.abramova.filemap;

import ru.fizteh.fivt.students.abramova.shell.*;
import java.io.IOException;

public class Main {
    public static void main (String[] args) {
        int exitValue = 0;
        FileMap fileMap = null;
        try {
            fileMap = new FileMap("db.dat", System.getProperty("fizteh.db.dir"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        String[][] argumentsForShell = null;
        if (args.length != 0) {
            argumentsForShell = Parser.parseArgs(args);
        }
        try {
            exitValue = new Shell(fileMap).doShell(argumentsForShell);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        System.exit(exitValue);
    }
}
