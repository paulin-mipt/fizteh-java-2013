package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class ShellMvCommand extends AbstractCommand<ShellState> {
    public ShellMvCommand() {
        super("mv", 2);
    }

    private static void move(File source, File destination) throws IOException {
        if (source.isFile()) {
            Files.copy(source.toPath(), destination.toPath().resolve(source.getName()));
        } else {
            File[] sourceContents = source.listFiles();

            destination.toPath().resolve(source.getName()).toFile().mkdirs();
            for (File content : sourceContents) {
                move(content, destination.toPath().resolve(source.getName()).toFile());
            }
        }

        source.delete();
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
            throw new IOException("MV ERROR: copying file or directory to itself is impossible");
        }
        if (!source.exists()) {
            throw new FileNotFoundException("MV ERROR: not existing source \"" + source.getName() + "\"");
        }

        if (!destination.isDirectory() && source.isFile()) {
            if (destination.isFile()) {
                throw new IOException("MV ERROR: file with name of source file \"" + source.getName()
                        + "\" already exists in current destination");
            }

            Files.copy(source.toPath(), destination.toPath());
            source.delete();
        } else {
            if (!destination.exists()) {
                throw new FileNotFoundException("MV ERROR: not existing destination \""
                        + destination.getCanonicalFile().toString() + "\"");
            }
            if (destination.listFiles(filter).length != 0) {
                throw new IOException("MV ERROR: file with name of source file \"" + source.getName()
                        + "\" already exists in destination \"" + destination.getCanonicalFile().toString() + "\"");
            }

            if (source.isFile()) {
                Files.copy(source.toPath(), destination.toPath().resolve(source.getName()));
                source.delete();
            }

            if (source.isDirectory()) {
                if (destination.toPath().startsWith(source.toPath())) {
                    throw new IOException("MV ERROR: directory can`t be copied into itself");
                }

                move(source, destination);
                source.delete();
            }
        }

        while (!state.getCurState().exists()) {
            state.setCurState(state.getCurState().getParentFile());
        }
    }
}
