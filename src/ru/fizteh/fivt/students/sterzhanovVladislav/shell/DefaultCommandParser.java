package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

public class DefaultCommandParser extends CommandParser {
    public String[] parseArgs(String cmdLine) {
        return cmdLine.trim().split("[\t ]+");
    }
}
