package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.filemap.State;

import java.io.File;
import java.io.IOException;

public class RmCommand extends Command {
    private boolean recRemove(File file) throws IOException {
        if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                recRemove(innerFile);
            }
        }
        if (!file.delete()) {
            System.err.println("Error while deleting");
            return false;
        }
        return true;
    }
    public boolean exec(String[] args, State curState) throws IOException {
        ShellState myState = ShellState.class.cast(curState);
        if (args.length != 1) {
            System.err.println("Invalid arguments");
            return false;
        }
        File temp = new File(args[0]);
        if (!temp.isAbsolute()) {
            temp = new File(myState.workingDirectory, args[0]);
        }
        File file = temp;
        return recRemove(file);
    }

    public String getCmd() {
        return "rm";
    }
}
