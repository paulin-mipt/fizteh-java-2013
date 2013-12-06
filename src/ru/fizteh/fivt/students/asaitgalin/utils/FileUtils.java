package ru.fizteh.fivt.students.asaitgalin.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            for (File f: file.listFiles()) {
                deleteRecursively(f);
            }
        }
        if (!file.delete()) {
            throw new IOException("failed to remove file \"" + file.getName() + "\"");
        }
    }

}
