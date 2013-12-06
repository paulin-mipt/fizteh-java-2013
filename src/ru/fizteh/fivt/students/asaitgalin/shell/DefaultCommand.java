package ru.fizteh.fivt.students.asaitgalin.shell;

public abstract class DefaultCommand implements Command {
    @Override
    public String[] parseCommandLine(String s) {
        return s.split("\\s+");
    }
}

