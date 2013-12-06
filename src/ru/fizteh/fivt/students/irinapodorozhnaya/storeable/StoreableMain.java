package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Shell;

public class StoreableMain {


    public static void main(String[] args) {
    StoreableState st = null;
        try {
            st = new StoreableState(System.in, System.out);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        int status = Shell.startShell(args, st);
        System.exit(status);
    }

}
