package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.students.belousova.shell.Command;

public class CommandExit implements Command {
    private final String name = "exit";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("exit");
        System.exit(0);
    }
}