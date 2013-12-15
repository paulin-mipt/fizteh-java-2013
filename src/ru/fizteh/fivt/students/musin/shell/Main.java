package ru.fizteh.fivt.students.musin.shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {
        String pwd = System.getProperty("user.dir");
        Shell shell = new Shell(pwd, System.out);
        FileSystemRoutine.integrate(shell);
        if (args.length != 0) {
            System.exit(shell.runArgs(args));
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.exit(shell.run(br));
        }
    }
}
