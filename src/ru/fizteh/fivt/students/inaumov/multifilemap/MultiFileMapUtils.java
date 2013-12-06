package ru.fizteh.fivt.students.inaumov.multifilemap;

import java.io.File;

public class MultiFileMapUtils {
    public static boolean isCorrectDir(String dir) {
        if (dir == null) {
            return false;
        }

        File file = new File(dir);
        if (file.isFile()) {
            return false;
        }

        return true;
    }

    public static void deleteFile(File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }
        if (fileToDelete.isDirectory()) {
            for (final File file: fileToDelete.listFiles()) {
                deleteFile(file);
            }
        }
        fileToDelete.delete();
    }

}
