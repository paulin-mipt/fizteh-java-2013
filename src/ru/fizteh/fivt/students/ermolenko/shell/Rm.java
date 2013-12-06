package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Rm implements Command<ShellState> {

    public String getName() {

        return "rm";
    }

    public static void remove(Path thePath) throws IOException {

        File theFile = thePath.toFile();
        if (theFile.isFile()) {
            if (!theFile.delete()) {
                throw new IOException("can't delete");
            }

        } else if (theFile.isDirectory()) {
            File[] listOfFiles = theFile.listFiles();
            if (listOfFiles != null) {
                for (File listOfFile : listOfFiles) {
                    remove(listOfFile.toPath());
                }
            }
            if (!theFile.delete()) {
                throw new IOException("can't delete");
            }
        }
    }

    public void executeCmd(ShellState inState, String[] args) throws IOException {

        Path thePath = inState.getPath().resolve(args[0]);
        try {
            if (1 == args.length) {
                if (thePath.toFile().exists()) {
                    remove(thePath);
                }
            } else {
                throw new IOException("not allowed number of arguments");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            while (!thePath.toFile().isDirectory()) {
                thePath = thePath.getParent();
            }
            inState.setPath(thePath);
        }
    }
}