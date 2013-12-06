package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class ShellCpCommand extends AbstractCommand<ShellState> {
    public ShellCpCommand() {
        super("cp", 2);
    }

    private static void copy(File source, File destination) throws IOException {
        if (source.isFile()) {
            Files.copy(source.toPath(), destination.toPath().resolve(source.getName()));
        } else {
            File[] sourceContents = source.listFiles();

            destination.toPath().resolve(source.getName()).toFile().mkdirs();
            for (File content : sourceContents) {
                copy(content, destination.toPath().resolve(source.getName()).toFile());
            }
        }
    }

    @Override
    public void execute(String[] input, ShellState state) throws IOException {
        final File source = new File(state.getCurState().toPath().resolve(input[0]).toString());
        File destination = new File(state.getCurState().toPath().resolve(input[1]).toString());
        FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return file.getName().equals(source.getName());
            }
        };

        if (source.compareTo(destination) == 0) {
            throw new IOException("CP ERROR: copying file or directory to itself is impossible");
        }
        if (!source.exists()) {
            throw new FileNotFoundException("CP ERROR: not existing source \"" + source.getName() + "\"");
        }

        if (!destination.isDirectory() && source.isFile()) {
            if (destination.isFile()) {
                throw new IOException("CP ERROR: file with name of source file \"" + source.getName()
                        + "\" already exists in destination \"" + destination.getCanonicalFile().toString()
                        + "\"");
            }

            Files.copy(source.toPath(), destination.toPath());
        } else {
            if (!destination.exists()) {
                throw new FileNotFoundException("CP ERROR: not existing destination \""
                        + destination.getCanonicalFile().toString() + "\"");
            }
            if (destination.listFiles(filter).length != 0) {
                throw new IOException("CP ERROR: file with name of source file \"" + source.getName()
                        + "\" already exists in current destination");

            }

            if (source.isFile()) {
                Files.copy(source.toPath(), destination.toPath().resolve(source.getName()));
            }
            if (source.isDirectory()) {
                if (destination.toPath().startsWith(source.toPath())) {
                    throw new IOException("CP ERROR: directory can`t be copied into itself");
                }

                copy(source, destination);
            }
        }
    }
}
