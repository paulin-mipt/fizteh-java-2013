package ru.fizteh.fivt.students.ryabovaMaria.shell;

import java.io.File;
import java.nio.file.Path;

public class DeleteDir {
    public static void delete(Path name) throws Exception {
        if (name.toFile().isDirectory()) {
            String[] list = name.toFile().list();
            for (int i = 0; i < list.length; ++i) {
                File curFile = new File(list[i]);
                Path temp = name.resolve(curFile.toPath()).normalize();
                delete(temp);
            }
            if (!name.toFile().delete()) {
                throw new Exception("delete: I can't delete this file.");
            }
        } else {
            if (!name.toFile().delete()) {
                throw new Exception("delete: I can't delete this file.");
            }
        }
    }
}
