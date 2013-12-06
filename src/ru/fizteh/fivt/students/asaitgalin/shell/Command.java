package ru.fizteh.fivt.students.asaitgalin.shell;

import java.io.IOException;

public interface Command {
    public String getName();
    public String[] parseCommandLine(String s);
    public void execute(String[] args) throws IOException;
    public int getArgsCount();
}
