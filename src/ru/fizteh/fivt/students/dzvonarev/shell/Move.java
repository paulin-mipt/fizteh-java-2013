package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Move implements CommandInterface {

    private static boolean isMoved(String source, String destination) {
        File sourceFile = new File(source);
        if (!sourceFile.isFile() && !sourceFile.isDirectory()) {
            return false;
        }
        if (sourceFile.isFile() || sourceFile.isDirectory()) {
            File destFile = new File(destination);
            if (destFile.isDirectory()) {
                return sourceFile.renameTo(new File(destination + File.separator + sourceFile.getName()));
            } else {
                return !destFile.isFile() && sourceFile.renameTo(new File(destination));
            }
        }
        return true;
    }

    public void execute(ArrayList<String> args) throws IOException {
        String expr = args.get(0);
        int spaceIndex = expr.indexOf(' ', 0);
        while (expr.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex == -1) {
            throw new IOException("mv: wrong parameters");
        }
        int index = newSpaceIndex;
        String source = Shell.getAbsPath(expr.substring(spaceIndex + 1, index));
        while (expr.indexOf(' ', newSpaceIndex + 1) == newSpaceIndex + 1) {
            ++newSpaceIndex;
        }
        if (expr.indexOf(' ', newSpaceIndex + 1) != -1) {
            throw new IOException("mv: wrong parameters");
        }
        String destination = Shell.getAbsPath(expr.substring(newSpaceIndex + 1, expr.length()));
        if (destination.contains(source)) {    // if parent into child
            throw new IOException("mv: can't move " + source);
        }
        if (!isMoved(source, destination)) {
            throw new IOException("mv: can't move " + source);
        }
    }

}
