package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.filemap.State;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CdCommand extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        ShellState myState = ShellState.class.cast(curState);
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            return false;
        }
        Path curPath = Paths.get(args[0]);
        if (myState.workingDirectory != null && !curPath.isAbsolute()) {
            curPath = Paths.get(myState.workingDirectory).resolve(curPath);
        }
        curPath = curPath.normalize();
        if (!curPath.toFile().exists()) {
            System.err.println("No such directory");
            return false;
        }
        myState.workingDirectory = curPath.toString();
        return true;
    }

    public String getCmd() {
        return "cd";
    }
}
