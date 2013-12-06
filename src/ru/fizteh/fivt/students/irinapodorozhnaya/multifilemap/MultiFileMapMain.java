package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.IOException;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Shell;

public class MultiFileMapMain {

    public static void main(String[] args) {
        MultiFileMapState st = null;
        try {
            st = new MultiFileMapState(System.in, System.out);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        int status = Shell.startShell(args, st);
        System.exit(status);
    }
}
