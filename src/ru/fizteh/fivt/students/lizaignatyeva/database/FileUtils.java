package ru.fizteh.fivt.students.lizaignatyeva.database;


import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static void remove(File path) throws IllegalArgumentException {
        String name = path.getName();
        if (!path.exists()) {
            throw new IllegalArgumentException(name + ": No such file or directory");
        }
        File[] children = path.listFiles();
        if (children != null) {
            if (path.isDirectory()) {
                for (File child : children) {
                    remove(child);
                }
            }
        }
        if (!path.delete()) {
            throw new IllegalArgumentException(name + ": Can't delete");
        }
    }

    public static File mkDir(String directoryName) throws IllegalArgumentException {
        File directory = new File(directoryName);
        if (!directory.isAbsolute()) {
            throw new IllegalArgumentException(directory.toString() + " is not absolute");
        }
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException(directory.toString() + " exists, but is not a directory");
            }
            return directory;
        }
        if (!directory.mkdir()) {
            throw new IllegalArgumentException("failed to create directory" + directory.toString());
        }

        return directory;

    }

    public static File mkFile(File directory, String name) throws IllegalArgumentException {
        File file = new File(directory.getAbsolutePath() + File.separator + name);
        if (file.exists()) {
            if (!file.isFile()) {
                throw new IllegalArgumentException(name + "exists, but is not a file");
            }
            return file;
        }
        try {
            if (!file.createNewFile()) {
                throw new IllegalArgumentException("failed to create file " + name);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to create file " + name);
        }

        return file;

    }
}

