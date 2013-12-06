package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.filemap.State;

import java.io.File;
import java.io.IOException;

public class DirCommand extends Command {
    public final boolean exec(String[] args, State curState) throws IOException {
        ShellState myState = ShellState.class.cast(curState);
        if (args.length != 0) {
            System.out.println("Invalid number of arguments");
            return false;
        }
        try {
            String [] listFile = new File(myState.workingDirectory).list();
            for (String child : listFile) {
                System.out.println(child);
            }
        } catch (Exception e) {
            System.out.println("Error with command dir");
            return false;
        }
        return true;
    }

    public String getCmd() {
        return "dir";
    }
}
