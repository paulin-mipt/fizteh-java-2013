package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CloseFile {
    public static void closeFile(RandomAccessFile file) throws IOException {
        try {
            if (file != null) {
                file.close();
            }
        } catch (IOException e2) {
            throw new IOException("Can't close file ", e2);
        }
    }
}
