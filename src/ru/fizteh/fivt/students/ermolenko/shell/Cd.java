package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Cd implements Command<ShellState> {

    public String getName() {

        return "cd";
    }

    public void executeCmd(ShellState inState, String[] args) throws IOException {

        if (1 == args.length) {
            Path thePath = inState.getPath().resolve(args[0]);
            File theFile = new File(thePath.toString());
            if (!theFile.isDirectory()) {
                throw new IOException("Directory doesn't exist");
            }
            inState.setPath(thePath.normalize());
        } else {
            throw new IOException("not allowed number of arguments");
        }
    }
}
