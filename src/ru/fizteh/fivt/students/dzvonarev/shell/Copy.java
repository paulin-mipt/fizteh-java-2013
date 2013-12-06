package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class Copy implements CommandInterface {

    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel;
        FileChannel destChannel;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destChannel.close();
        } catch (IOException e) {
            throw new IOException("cp: can't copy file " + source);
        }
    }

    private static boolean simpleFolderToFolderCopy(String source, String destination) {
        String folder = destination + File.separator + (new File(source)).getName();
        File dir = new File(folder);
        return dir.mkdir();
    }

    private static void recursionCopy(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            if (!simpleFolderToFolderCopy(source.toString(), dest.toString())) {
                throw new IOException("cp: can't copy file " + source);
            }
            String[] file = source.list();
            for (String aFile : file) {
                File currFile = new File(source, aFile);
                File newDest = new File(dest.toString() + File.separator + source.getName());
                recursionCopy(currFile, newDest);
            }
        } else {
            if (!isFileToFolder(source.toString(), dest.toString())) {
                throw new IOException("cp: can't copy file " + source);
            }
        }
    }

    private static boolean isFileToFile(String source, String destination) {
        return (new File(source)).isFile() && (new File(destination)).isFile();
    }

    private static boolean isFileToFolder(String source, String destination) throws IOException {
        if (destination.equals(Shell.getCurrentDirectory())) {
            throw new IOException("cp: can't copy file " + source);
        }
        if (new File(source).isFile()) {
            String fileName = (new File(source)).getName();
            String toChannel = destination;
            File newFile = new File(destination);
            if (!(new File((new File(destination)).getParent())).exists()) {
                Mkdir mkdir = new Mkdir();
                ArrayList<String> args = new ArrayList<String>();
                args.add(" " + (new File(destination)).getParent());
                mkdir.execute(args);
            }
            if (new File(destination).exists() && (new File(destination)).isDirectory()) {
                newFile = new File(destination + File.separator + fileName);
                toChannel = destination + File.separator + fileName;
            }
            try {
                if (!newFile.createNewFile()) {
                    throw new IOException("cp: can't copy file " + source);
                }
            } catch (IOException e) {
                throw new IOException("cp: can't copy file " + source);
            }
            copyFileUsingChannel(new File(source), new File(toChannel));
            return true;
        } else {
            return false;
        }
    }

    private static boolean isFolderToFolder(String source, String destination) throws IOException {
        if (new File(source).isDirectory()) {
            if (!(new File(destination)).exists()) {
                Mkdir mkdir = new Mkdir();
                ArrayList<String> args = new ArrayList<>();
                args.add(" " + destination);
                mkdir.execute(args);
            }
            if (destination.contains(source)) {    // if parent into child
                throw new IOException("cp: can't copy " + source);
            }
            try {
                recursionCopy(new File(source), new File(destination));
            } catch (IOException e) {
                throw new IOException("cp: can't copy " + source);
            }
            return true;
        } else {
            return false;
        }
    }

    private static boolean isFolderToFile(String source, String destination) {
        return ((new File(source).isDirectory()) && (new File(destination).isFile()));
    }

    public void execute(ArrayList<String> args) throws IOException {
        String expr = args.get(0);
        int spaceIndex = expr.indexOf(' ', 0);
        while (expr.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex == -1) {
            throw new IOException("cp: wrong parameters");
        }
        int index = newSpaceIndex;
        String source = Shell.getAbsPath(expr.substring(spaceIndex + 1, index));
        while (expr.indexOf(' ', newSpaceIndex + 1) == newSpaceIndex + 1) {
            ++newSpaceIndex;
        }
        if (expr.indexOf(' ', newSpaceIndex + 1) != -1) {
            throw new IOException("cp: wrong parameters");
        }
        String destination = Shell.getAbsPath(expr.substring(newSpaceIndex + 1, expr.length()));
        if (destination.equals(source)) {
            throw new IOException("cp: can't copy file " + source);
        }
        boolean error = true;
        boolean checked = false;
        if (isFileToFile(source, destination)) {
            throw new IOException("cp: can't copy file " + source);
        }
        if (isFolderToFile(source, destination)) {
            throw new IOException("cp: can't copy file " + source);
        }
        if (isFileToFolder(source, destination)) {
            error = false;
            checked = true;
        }
        if (isFolderToFolder(source, destination) && !checked) {
            error = false;
        }
        if (error) {
            throw new IOException("cp: can't copy file " + source);
        }
    }

}
