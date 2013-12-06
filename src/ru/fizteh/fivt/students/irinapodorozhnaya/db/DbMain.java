package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Shell;

public class DbMain {
    public static void main(String[] args) {
        FileMapState st = null;
        try {
            st = new FileMapState(System.in, System.out);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        int status = Shell.startShell(args, st);
        System.exit(status);
    }   
}

