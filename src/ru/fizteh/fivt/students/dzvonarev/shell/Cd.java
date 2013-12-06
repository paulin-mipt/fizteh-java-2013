package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Cd implements CommandInterface {

    public void execute(ArrayList<String> args) throws IOException {
        String expr = args.get(0);
        int spaceIndex = expr.indexOf(' ', 0);
        if (expr.indexOf(' ', spaceIndex + 1) != -1) {
            throw new IOException("cd: wrong parameters");
        }
        String destination = Shell.getAbsPath(expr.substring(spaceIndex + 1, expr.length()));
        File destFile = new File(destination);
        if (destFile.isDirectory()) {
            Shell.changeCurrentDirectory(destination);
        } else {
            throw new IOException("cd: can't change directory to " + expr.substring(spaceIndex + 1, expr.length()));
        }
    }

}
