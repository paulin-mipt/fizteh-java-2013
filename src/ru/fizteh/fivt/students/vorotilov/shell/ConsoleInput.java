package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.IOException;
import java.util.Queue;

public abstract class ConsoleInput {

    protected Queue<String[]> commandsBuffer;

    public abstract String[] getNext() throws IOException, NoNextCommand, ExitCommand;

    protected String[] parseCommand(String input) {
        return input.trim().split("\\s+");
    }

    public boolean hasNext() {
        return true;
    }
}
