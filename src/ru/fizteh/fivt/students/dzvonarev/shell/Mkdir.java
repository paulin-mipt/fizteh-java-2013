package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Mkdir implements CommandInterface {

    public void execute(ArrayList<String> args) throws IOException {
        String expr = args.get(0);
        if (expr.equals("mkdir")) {
            throw new IOException("mkdir: wrong  parameters");
        }
        int spaceIndex = expr.indexOf(' ', 0);
        int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex != -1) {
            throw new IOException("mkdir: wrong parameters");
        }
        String folder = Shell.getAbsPath(expr.substring(spaceIndex + 1, expr.length()));
        File dir = new File(folder);
        if (!dir.mkdir()) {
            throw new IOException("mkdir: can't create " + expr.substring(spaceIndex + 1, expr.length()) + " folder");
        }
    }

}
