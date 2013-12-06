package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Dir implements CommandInterface {

    public void execute(ArrayList<String> args) throws IOException {
        String expr = args.get(0);
        if (!expr.equals("dir")) {
            throw new IOException("dir: wrong parameters");
        }
        File dir = new File(Shell.getCurrentDirectory());
        String[] path = dir.list();
        for (String file : path) {
            File currFile = new File(file);
            if (!currFile.isHidden()) {
                System.out.println(file);
            }
        }
    }

}
