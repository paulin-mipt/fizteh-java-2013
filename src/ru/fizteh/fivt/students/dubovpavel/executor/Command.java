package ru.fizteh.fivt.students.dubovpavel.executor;

import java.util.ArrayList;

public class Command {
    private String header;
    private ArrayList<String> body;
    private int index;

    public Command(String header, int index) {
        this.header = header;
        this.index = index;
        body = new ArrayList<String>();
    }

    public Command(Command other) {
        this(other.header, other.index);
    }

    public void addArgument(String arg) {
        body.add(arg);
    }

    public String getHeader() {
        return header;
    }

    public String getArgument(int index) {
        return body.get(index);
    }

    public int argumentsCount() {
        return body.size();
    }

    public String getDescription() {
        return String.format("Command #%d", index);
    }
}
