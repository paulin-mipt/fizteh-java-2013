package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CpCommand implements Command {

    private ShellState curState;

    public CpCommand(ShellState state) {
        curState = state;

    }

    public String getName() {
        return "cp";
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
            throw new IOException("cp: " + source.getCanonicalPath() + " was not found");
        }
        if (source.equals(destination)) {
            throw new IOException("cp: " + args[1] + " and " + args[2] + " are same files");
        }
        if (source.isFile()) {
            if (!destination.exists()) {
                Files.copy(source.toPath(), destination.toPath());
            } else {
                if (destination.isDirectory()) {
                    File target = new File(destination, source.getName());
                    if (target.exists()) {
                        throw new IOException("cp: " + source.getName() + " already exists in "
                                + destination.getName());
                    } else {
                        Files.copy(source.toPath(), target.toPath());
                    }
                } else {
                    throw new IOException("cp: " + args[2] + " already exists");
                }
            }
        }
        if (source.isDirectory()) {
            if (destination.isDirectory()) {
                if (!destination.exists()) {
                    destination.mkdir();
                }
                copyingRec(source, destination);
            } else {
                throw new IOException("cp: " + "it's impossible to copy directory " + source.getName()
                        + " to file " + destination.getName());
            }
        }
    }

    private void copyingRec(File src, File dest) throws IOException {
        File target = new File(dest, src.getName());
        if (target.exists()) {
            throw new IOException("cp: " + src.getName() + " already exists in" + dest.getName());
        } else {
            if (src.isDirectory()) {
                target.mkdir();
                for (File file : src.listFiles()) {
                    copyingRec(file, target);
                }
            } else {
                Files.copy(src.toPath(), target.toPath());
            }
        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 2);
    }
}
