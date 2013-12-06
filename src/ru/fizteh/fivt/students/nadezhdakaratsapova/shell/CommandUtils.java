package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.io.IOException;

public class CommandUtils {

    public static void recDeletion(File src) throws IOException {
        if (src.isDirectory()) {
            File[] listFile = src.listFiles();
            if (listFile.length > 0) {
                for (File file : listFile) {
                    recDeletion(file);
                }
            }
        }
        if (!src.delete()) {
            throw new IOException("not managed to remove " + src.getName());
        }
    }
}
