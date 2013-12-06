package ru.fizteh.fivt.students.adanilyak.tools;

import java.io.File;
import java.io.IOException;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 15:45
 */
public class DeleteDirectory {
    private static void recursiveDeletePart(File startPoint) throws IOException {
        File[] listOfFiles = startPoint.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                recursiveDeletePart(file);
            }
        }

        if (!startPoint.delete()) {
            throw new IOException("Can not delete directory, unknown error");
        }
    }

    public static void rm(File directory) throws IOException {
        if (!directory.exists()) {
            throw new IOException("File or directory do not exist");
        }
        recursiveDeletePart(directory);
    }
}
