package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.IOException;

public interface Command {
    String getName();

    void execute(String[] args) throws IOException;

    boolean compareArgsCount(int inputArgsCount);
}
