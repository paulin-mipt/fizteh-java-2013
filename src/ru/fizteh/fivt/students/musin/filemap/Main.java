package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.students.musin.shell.Shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {
        String pwd = System.getProperty("user.dir");
        String db = System.getProperty("fizteh.db.dir");
        if (db == null) {
            System.err.println("Database file not specified");
            System.exit(-1);
        }
        Shell shell = new Shell(pwd, System.out);
        try {
            FileMapProvider provider = new FileMapProviderFactory().create(db);
            ShellSuperDatabaseHandler serverDatabase = new ShellSuperDatabaseHandler(provider);
            serverDatabase.integrate(shell);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        int exitCode = 0;
        if (args.length != 0) {
            exitCode = shell.runArgs(args);
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            exitCode = shell.run(br);
        }
        System.exit(exitCode);
    }
}

