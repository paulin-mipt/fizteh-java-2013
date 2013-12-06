package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MvCommand implements Command {

    private ShellState curState;

    public MvCommand(ShellState state) {
        curState = state;

    }

    public String getName() {
        return "mv";
    }

    public void execute(String[] args) throws IOException {
        File source = new File(args[1]);
        if (!source.isAbsolute()) {
            source = new File(curState.getCurDir(), args[1]);
        }
        File destination = new File(args[2]);
        if (!destination.isAbsolute()) {
            destination = new File(curState.getCurDir(), args[2]);
        }
        if (!source.exists()) {
            throw new IOException("mv: " + args[1] + " was not found");
        }
        if (source.equals(destination)) {
            throw new IOException("mv: " + args[1] + " and " + args[2] + " are same files");
        }

        if (source.isFile()) {
            if (destination.exists()) {
                if (destination.isDirectory()) {
                    File target = new File(destination, source.getName());
                    if (target.exists()) {
                        throw new IOException("mv: " + source.getName()
                                + " already exists in " + destination.getName());
                    }
                    Files.move(source.toPath(), target.toPath());
                } else {
                    throw new IOException("mv: not managed to copy File " + source.getName()
                            + " to File " + destination.getName());
                }
            } else {
                if (source.getParentFile().equals(destination.getParentFile())) {
                    source.renameTo(destination);
                } else {
                    throw new IOException("mv: not managed to copy File " + source.getName()
                            + " to File" + destination.getName());
                }
            }
        } else {
            if (destination.isDirectory()) {
                File target = new File(destination, source.getName());
                if (target.exists()) {
                    throw new IOException("mv: " + source.getName() + " already exists in " + destination.getName());
                }
                moveRec(source, destination);
            } else {
                if (!destination.exists() && source.getParentFile().equals(destination.getParentFile())) {
                    source.renameTo(destination);
                } else {
                    throw new IOException("mv: not managed to copy " + source.getName()
                            + " to " + destination.getName());
                }
            }
        }

    }

    private void moveRec(File src, File dest) throws IOException {
        File target = new File(dest, src.getName());
        if (src.isDirectory()) {
            target.mkdir();
            File[] fileList = src.listFiles();
            if (fileList.length > 0) {
                for (File file : fileList) {
                    moveRec(file, target);
                }
            }
            src.delete();
        } else {
            Files.move(src.toPath(), target.toPath());
        }

    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 2);
    }
}
