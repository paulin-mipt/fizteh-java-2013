package ru.fizteh.fivt.students.adanilyak.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CmdCp {
    private final RequestCommandType name = RequestCommandType.getType("cp");
    private final int amArgs = 2;

    public RequestCommandType getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    private void recursiceCopyPart(File startPoint, File destination) throws Exception {
        File[] listOfFiles = startPoint.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    File tempDestination = new File(destination.getAbsolutePath(), file.getName());
                    Files.copy(file.toPath(), tempDestination.toPath());
                } else {
                    File newDir = new File(destination, file.getName());
                    newDir = newDir.toPath().normalize().toFile();
                    if (!newDir.getCanonicalFile().mkdir()) {
                        throw new Exception("Can not create directory: " + name);
                    }
                    recursiceCopyPart(file, newDir);
                }
            }
        }
    }

    public void work(String arg1, String arg2, Shell shell) throws Exception {
        File source = new File(shell.getState(), arg1);
        source = source.toPath().normalize().toFile();
        File destination = new File(shell.getState(), arg2);
        destination = destination.toPath().normalize().toFile();

        String compare = destination.toPath().relativize(source.toPath()).toString();
        if (compare.matches("[\\\\./]+") || compare.equals("")) {
            throw new IOException("Can not copy folder(file) to itself or to its child-folder");
        }
        if (!source.exists()) {
            throw new IOException("Source file or directory do not exist");
        }
        if (destination.isFile() && destination.exists()) {
            if (source.isFile()) {
                throw new IOException("Can not copy file to existed file");
            }
            throw new IOException("Can not copy directory to existed file");
        }
        if (source.isFile() && destination.isDirectory()) {
            destination = new File(destination.getAbsolutePath(), source.getName());
            Files.copy(source.toPath(), destination.toPath());
        }
        if (source.isFile()/* && destination.isFile()*/) {
            Files.copy(source.toPath(), destination.toPath());
        }
        if (destination.isDirectory()) {
            File newDir = new File(destination, arg1);
            newDir = newDir.toPath().normalize().toFile();
            if (!newDir.getAbsoluteFile().mkdir()) {
                throw new Exception("Can not create directory: " + destination.getName());
            }
            recursiceCopyPart(source, newDir);
        }
    }
}
