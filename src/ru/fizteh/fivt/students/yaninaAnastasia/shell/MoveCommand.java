package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.filemap.State;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MoveCommand extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        ShellState myState = ShellState.class.cast(curState);
        if (args.length != 2) {
            System.err.println("Invalid arguments");
            return false;
        }
        File temp = new File(args[0]);
        if (!temp.isAbsolute()) {
            temp = new File(myState.workingDirectory, args[0]);
        }
        File source = temp;
        temp = new File(args[1]);
        if (!temp.isAbsolute()) {
            temp = new File(myState.workingDirectory, args[1]);
        }
        File destination = temp;
        if (!source.exists()) {
            System.err.println("The directory doesn't exist");
            return false;
        }
        if (destination.isDirectory()) {
            destination = new File(destination, source.getName());
        }
        if (destination.exists()) {
            System.err.println("Error while moving");
            return false;
        }
        try {
            Files.move(source.toPath(), destination.toPath());
        } catch (IOException e) {
            System.err.println("Error while moving");
            return false;
        }
        return true;
    }

    public String getCmd() {
        return "mv";
    }
}
