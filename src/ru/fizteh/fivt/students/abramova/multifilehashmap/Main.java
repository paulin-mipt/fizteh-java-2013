package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.shell.Parser;
import ru.fizteh.fivt.students.abramova.shell.Shell;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main (String[] args) {
        int exitValue = 0;
        MultiFileMap multiMap = null;
        String property = System.getProperty("fizteh.db.dir");
        File root = null;
        if (property != null) {
            root = new File(property);
        }
        if (property == null || !root.exists() || !root.isDirectory()) {
            System.err.println("No such directory");
            System.exit(-1);
        }
        try {
            multiMap = new MultiFileMap(System.getProperty("fizteh.db.dir"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        String[][] argumentsForShell = null;
        if (args.length != 0) {
            argumentsForShell = Parser.parseArgs(args);
        }
        try {
            exitValue = new Shell(multiMap).doShell(argumentsForShell);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        try {
            multiMap.closeWorkingTable();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        System.exit(exitValue);
    }
}
