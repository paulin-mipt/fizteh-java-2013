package ru.fizteh.fivt.students.paulinMatavina.shell;

import java.io.File;
import java.io.IOException;

public class ShellUtils {
    public static int moveCopyCheck(final File source, final File dest) {
        if (!source.exists()) {
            System.err.println("mv or cp: no such file");
            return 1;
        }
        if (dest.exists()) {
            System.err.println("mv or cp: writing on an existing file");
            return 1;
        }
        String canonicalSource = "";
        String parentDest = "";
        try {
            canonicalSource = source.getCanonicalPath();
            parentDest = dest.getParentFile().getCanonicalPath();
        } catch (IOException e) {
            System.err.println("mv or cp: internal error: " + e.getMessage());
            return 1;            
        }

        if (parentDest.startsWith(canonicalSource)) {
            System.err.println("mv or cp: attempt to move a folder into itself");
            return 1;   
        }
        return 0;
    }
}
