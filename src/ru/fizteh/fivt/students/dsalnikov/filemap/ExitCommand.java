package ru.fizteh.fivt.students.dsalnikov.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.dsalnikov.shell.Command;

public class ExitCommand implements Command {
    public String getName() {
        return "exit";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(Object f, String[] st) throws IOException {
            FileMapState filemap = (FileMapState)f;
        if (st.length != 1) {
            throw new IllegalArgumentException("Incorrect usage of Command exit: wrong amount of arguments");
        } else {
            filemap.put(filemap.getState());
            System.exit(0);
        }
    }
}
