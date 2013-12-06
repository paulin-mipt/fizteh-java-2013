package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

public abstract class ShellCommand extends Command {
    public CommandParser getParser() {
        return new DefaultCommandParser();
    }

    ShellCommand(int argc) {
        super(argc);
    }
}
