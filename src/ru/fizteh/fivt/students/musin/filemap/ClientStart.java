package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.students.musin.shell.Shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientStart {

    public static void main(String[] args) throws Exception {
        String pwd = System.getProperty("user.dir");
        ShellClientDatabaseHandler database = new ShellClientDatabaseHandler(new RemoteFileMapProviderFactory());
        Shell shell = new Shell(pwd, System.out);
        database.integrate(shell);
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
