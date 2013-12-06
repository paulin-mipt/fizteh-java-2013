package ru.fizteh.fivt.students.dsalnikov.shell;

import java.io.File;
import java.io.IOException;

public class CdCommand implements Command {
    public String getName() {
        return "cd";
    }

    public int getArgsCount() {
        return 1;
    }

    public void execute(Object shell, String[] s) throws IOException {
        if (s.length != 2) {
            throw new IllegalArgumentException("Wrong usage of Command cd : incorrect amount of arguments");
        } else {
            ShellState sh = (ShellState)shell;
            String currstate = sh.getState();
            String cdstate = s[1];
            File newdirectory = new File(s[1]);
            if (!newdirectory.isAbsolute()) {
                newdirectory = new File(sh.getState(), cdstate);
            }
            if (newdirectory.exists() && newdirectory.isDirectory()) {
                File curr = newdirectory.getCanonicalFile();
                sh.setState(curr.getAbsolutePath());
            } else {
                throw new IOException("'" + cdstate + "' : No such file or directory exists");
            }
        }
    }
}
