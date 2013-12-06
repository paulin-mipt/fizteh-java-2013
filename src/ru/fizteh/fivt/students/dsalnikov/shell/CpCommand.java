package ru.fizteh.fivt.students.dsalnikov.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

public class CpCommand implements Command {
    public String getName() {
        return "cp";
    }

    public int getArgsCount() {
        return 2;
    }

    public void copyFiles(ShellState sh, String src, String dest) throws IOException {

        File source = new File(src);
        if (!source.isAbsolute()) {
            source = new File(sh.getState(), src);
        }
        if (!source.exists()) {
            throw new FileNotFoundException(source.getCanonicalPath() + ":No such file or directory");
        }
        File destination = new File(dest);
        if (!destination.isAbsolute()) {
            destination = new File(sh.getState(), dest);
        }
        if (destination.exists()) {
            throw new FileAlreadyExistsException("Can't copy: file" + destination.getName());
        }
        if (source.isDirectory()) {
            destination.mkdir();
            destination.createNewFile();
            if (!destination.exists()) {
                throw new FileNotFoundException("Unable to copy file:" + source.getCanonicalPath() + " to "
                        + destination.getPath());
            }
        }

        if (source.isDirectory()) {
            if (source.list().length != 0) {
                String files[] = source.list();
                for (String s : files) {
                    File tempsrc = new File(source, s);
                    File tempdst = new File(dest, s);
                    copyFiles(sh, tempsrc.toPath().toString(), tempdst.toPath().toString());
                }
            }
        } else {
            Files.copy(source.toPath(), destination.toPath());
            if (!destination.exists()) {
                throw new FileNotFoundException("Unable to copy file:" + source.getCanonicalPath() + " to "
                        + destination.getPath());
            }
        }
    }


    public void execute(Object shell, String[] str) throws IOException {
        if (str.length != 3) {
            throw new IllegalArgumentException("Illegal arguments");
        } else {
            ShellState sh = (ShellState)shell;
            copyFiles(sh, str[1].toString(), str[2].toString());
        }
    }
}
