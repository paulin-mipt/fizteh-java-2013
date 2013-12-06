package ru.fizteh.fivt.students.vlmazlov.utils;

import java.io.*;

public class FileUtils {

    public static File createTempDir(String prefix, String suffix) {
        File tmp = null;
        try {
            tmp = File.createTempFile(prefix, suffix);
        } catch (IOException ex) {
            return null;
        }

        if (!tmp.delete()) {
            return null;
        }
        if (!tmp.mkdir()) {
            return null;
        }
        return tmp;
    }

    public static void copyToDir(File sourceFile, File destinationDir) throws FileOperationFailException {
        File copiedFile = new File(destinationDir, sourceFile.getName());

        try {
            copiedFile.createNewFile();
        } catch (IOException ex) {
            throw new FileOperationFailException(ex.getMessage());
        }

        copyToFile(sourceFile, copiedFile);
    }

    public static void copyToFile(File sourceFile, File copiedFile) throws FileOperationFailException {
        FileInputStream original = null;
        FileOutputStream copy = null;

        try {
            if (!copiedFile.exists()) {
                copiedFile.createNewFile();
            }

            original = new FileInputStream(sourceFile);
            copy = new FileOutputStream(copiedFile);

            byte[] buffer = new byte[4028];
            int bytesRead;

            while ((bytesRead = original.read(buffer)) > 0) {
                copy.write(buffer, 0, bytesRead);
            }

        } catch (FileNotFoundException ex) {
            throw new FileOperationFailException(ex.getMessage());
        } catch (IOException ex) {
            throw new FileOperationFailException(ex.getMessage());
        } finally {
            QuietCloser.closeQuietly(original);
            QuietCloser.closeQuietly(copy);
        }
    }

    public static File getAbsFile(String file, String dir) {
        File absFile = new File(file);

        if (!absFile.isAbsolute()) {
            absFile = new File(new File(dir), file);
        }

        return absFile;
    }

    public static void recursiveCopy(File source, File destination) throws FileOperationFailException {
        if (source.isFile()) {
            FileUtils.copyToDir(source, destination);
            return;
        }

        File newDestination = new File(destination, source.getName());
        if (!newDestination.exists()) {
            if (!newDestination.mkdir()) {
                throw new FileOperationFailException("Unable to create directory: " + source.getName());
            }
        }

        for (String toCopy : source.list()) {
            recursiveCopy(new File(source, toCopy), newDestination);
        }
    }

    public static void recursiveDelete(File toDelete) {
        if ((toDelete.isFile()) || (0 == toDelete.list().length)) {
            toDelete.delete();
            return;
        }

        String[] listing = toDelete.list();

        for (String entry : listing) {
            recursiveDelete(new File(toDelete, entry));
        }

        toDelete.delete();
    }

    public static void moveToDir(File source, File destination) throws FileOperationFailException {
        if (source.isFile()) {
            FileUtils.copyToDir(source, destination);
        } else {
            File newDestination = new File(destination, source.getName());
            if (!newDestination.exists()) {
                if (!newDestination.mkdir()) {
                    throw new FileOperationFailException("Unable to create directory: " + source.getName());
                }
            }

            for (String toMove : source.list()) {
                moveToDir(new File(source, toMove), newDestination);
            }
        }

        source.delete();
    }
}
