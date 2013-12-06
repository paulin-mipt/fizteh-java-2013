package ru.fizteh.fivt.students.adanilyak.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CmdMv {
    private final RequestCommandType name = RequestCommandType.getType("mv");
    private final int amArgs = 2;

    public RequestCommandType getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    public void work(String arg1, String arg2, Shell shell) throws Exception {
        File source = new File(shell.getState(), arg1);
        source = source.toPath().normalize().toFile();
        File destination = new File(shell.getState(), arg2);
        destination = destination.toPath().normalize().toFile();

        String compare = destination.toPath().relativize(source.toPath()).toString();
        if (compare.matches("[\\\\./]+") || compare.equals("")) {
            throw new IOException("Can not move folder(file) to itself or to its child-folder");
        }

        if (!source.exists()) {
            throw new IOException("Source file or directory do not exist");
        }
        if (destination.exists() && destination.isFile()) {
            throw new IOException("Can not move folde(file) to existed file");
        }
        if (source.isFile() && destination.isFile()) {
            Files.move(source.toPath(), destination.toPath());
            return;
        }
        if (source.isFile() && destination.exists()) {
            destination = new File(destination.getAbsolutePath(), source.getName());
            Files.move(source.toPath(), destination.toPath());
            return;
        }
        if (source.isDirectory() && destination.isDirectory()) {
            source.renameTo(new File(destination.getAbsolutePath(), source.getName()));
        }
    }
}
