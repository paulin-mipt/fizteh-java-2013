package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.filemap.State;

import java.io.IOException;

public abstract class Command {
    public Command() {

    }

    public abstract boolean exec(String[] args, State curState) throws IOException;

    public abstract String getCmd();
}
