package ru.fizteh.fivt.students.elenav.utils;

import java.io.File;
import java.io.IOException;

public class Functions {
    
    public static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteRecursively(file);
                }
            }
        }
        if (!f.delete()) {
            throw new IOException("rm: cannot remove '" + f.getName() + "': Unknown error");
        }
    }
    
}
