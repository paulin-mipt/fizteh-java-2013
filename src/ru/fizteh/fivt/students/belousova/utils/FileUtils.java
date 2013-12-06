package ru.fizteh.fivt.students.belousova.utils;

import ru.fizteh.fivt.students.belousova.shell.ShellState;

import java.io.*;

public class FileUtils {

    public static void copy(File source, File destination) throws IOException {
        if (!source.exists()) {
            throw new IOException("cannot copy '" + source.getName() + "': No such file or directory");
        }
        if (destination.isFile() && source.isDirectory()) {
            throw new IOException("cannot overwrite non-directory '" + destination.getName()
                    + "' with directory '" + source.getName() + "'");
        }
        if (source.equals(destination)) {
            throw new IOException("you try to copy '" + source.getName() + "' to itself");
        }
        if (!destination.getParentFile().exists()) {
            throw new IOException("destination '" + destination.getName() + "' doesn't exist");
        }

        if (source.isFile()) {
            if (!destination.exists() || destination.isFile()) {
                copyFileToFile(source, destination);
            } else {
                copyFileToFolder(source, destination);
            }
        } else {
            copyFolderToFolder(source, destination);
        }
    }

    public static void copyFileToFile(File source, File destination) throws IOException {
        if (destination.exists()) {
            throw new IOException("failed to copy '" + destination.getName() + "': already exists");
        }
        try {
            destination.createNewFile();
            InputStream inputStream = new FileInputStream(source);
            try {
                OutputStream outputStream = new FileOutputStream(destination);
                try {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, read);
                    }
                } finally {
                    closeStream(outputStream);
                }
            } finally {
                closeStream(inputStream);
            }
        } catch (IOException e) {
            throw new IOException("cannot copy", e);
        }
    }

    public static void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            //do nothing
        }
    }

    public static void copyFileToFolder(File source, File destination) throws IOException {
        File copy = new File(destination, source.getName());
        copyFileToFile(source, copy);

    }

    public static void copyFolderToFolder(File source, File destination) throws IOException {
        File copy;
        if (!destination.exists()) {
            copy = destination;
        } else {
            copy = new File(destination, source.getName());
        }
        if (copy.exists()) {
            throw new IOException("failed to copy '" + copy.getName() + "': already exists");
        }
        copy.mkdirs();
        File[] files = source.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    copyFileToFolder(f, copy);
                }
                if (f.isDirectory()) {
                    copyFolderToFolder(f, copy);
                }
            }
        }
    }

    public static void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                deleteDirectory(f);
            }
        }
        boolean success = directory.delete();
        if (!success) {
            throw new IOException("cannot remove " + directory.getName() + ": unknown error");
        }
    }

    public static File getFileFromString(String s, ShellState state) {
        File file = new File(s);

        if (!file.isAbsolute()) {
            file = new File(state.getCurrentDirectory(), s);
        }
        return file;
    }

    public static void move(File source, File destination) throws IOException {
        if (!source.exists()) {
            throw new IOException("cannot move '" + source.getName() + "': No such file or directory");
        }
        if (destination.isFile() && source.isDirectory()) {
            throw new IOException("cannot overwrite non-directory '" + destination.getName()
                    + "' with directory '" + source.getName() + "'");
        }
        if (source.equals(destination)) {
            throw new IOException("you try to move '" + source.getName() + "' to itself");
        }
        if (!destination.getParentFile().exists()) {
            throw new IOException("destination '" + destination.getName() + "' doesn't exist");
        }
        if (!destination.exists() || destination.isFile()) {
            FileUtils.renameFile(source, destination);
        } else if (destination.isDirectory()) {
            FileUtils.moveToFolder(source, destination);
        }
    }

    public static void renameFile(File oldFile, File newFile) throws IOException {
        if (newFile.equals(oldFile)) {
            throw new IOException("cannot move '" + oldFile.getName() + "' to itself");
        }
        if (newFile.exists()) {
            boolean successDelete = newFile.delete();
            if (!successDelete) {
                throw new IOException("cannot overwrite");
            }
        }

        File parent = new File(newFile.getParent());

        if (parent.exists() && !parent.equals(oldFile)) {
            boolean successRename = oldFile.renameTo(newFile);
            if (!successRename) {
                throw new IOException("cannot rename");
            }
        } else {
            String newName = parent.getName() + newFile.getName();
            File newNewFile = new File(parent.getParent() + File.separator + newName);
            renameFile(oldFile, newNewFile);
        }
    }

    public static void moveToFolder(File source, File destination) throws IOException {
        if (source.isFile()) {
            copyFileToFolder(source, destination);
        }
        if (source.isDirectory()) {
            copyFolderToFolder(source, destination);
        }
        deleteDirectory(source);
    }
}
