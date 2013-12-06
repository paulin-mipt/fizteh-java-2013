package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Remove implements CommandInterface {

    private static void delete(File aim) throws IOException {
        if (aim.getName().equals(".") || aim.getName().equals("..")) {
            throw new IOException("rm: can't remove " + aim + " : forbidden");
        }
        if (aim.isDirectory()) {
            if (aim.list().length == 0) {
                if (!aim.delete()) {
                    throw new IOException("rm: can't remove " + aim + " : no such file or directory");
                }
            } else {
                String[] file = aim.list();
                for (String aFile : file) {
                    File currFile = new File(aim, aFile);
                    delete(currFile);
                }
                if (aim.list().length == 0) {
                    if (!aim.delete()) {
                        throw new IOException("rm: can't remove " + aim + " : no such file or directory");
                    }
                }
            }
        } else {
            if (!aim.delete()) {
                throw new IOException("rm: can't remove " + aim + " : no such file or directory");
            }
        }
    }

    public void execute(ArrayList<String> args) throws IOException {
        String expr = args.get(0);
        int spaceIndex = expr.indexOf(' ', 0);
        while (expr.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        if (expr.indexOf(' ', spaceIndex + 1) != -1) {
            throw new IOException("Wrong parametres of remove");
        }
        String path = Shell.getAbsPath(expr.substring(spaceIndex + 1, expr.length()));
        if (args.size() != 2                                                           // can't delete father of son
                && (path.equals(Shell.getCurrentDirectory()) || Shell.getCurrentDirectory().contains(path))) {
            throw new IOException("rm: can't remove " + path);
        }
        if ((new File(path)).isFile() || (new File(path)).isDirectory()) {
            delete(new File(path));
        } else {
            throw new IOException("rm: can't remove " + path);
        }
    }

}
