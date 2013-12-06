package ru.fizteh.fivt.students.dsalnikov.shell;

import java.io.IOException;

public class MvCommand implements Command {

    public String getName() {
        return "mv";
    }

    public int getArgsCount() {
        return 2;
    }

    public void execute(Object shell, String[] st) throws IOException {
        if (st.length != 3) {
            throw new IllegalArgumentException("Incorrect usage of Command mv : wrong amount of arguments");
        } else {
            Shell sh = (Shell)shell;
            String[] rmstr = new String[2];
            rmstr[1] = st[1];
            CpCommand a = new CpCommand();
            RmCommand b = new RmCommand();
            a.execute(sh, st);
            b.execute(sh, rmstr);
        }
    }
}
