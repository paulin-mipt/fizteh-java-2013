package ru.fizteh.fivt.students.kislenko.junit.test;

import java.io.File;

public class Cleaner {
    public static void clean(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                clean(f);
            }
        }
        dir.delete();
    }
}
