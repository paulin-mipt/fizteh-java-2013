package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

public class ExitCommand extends DefaultCommand {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
