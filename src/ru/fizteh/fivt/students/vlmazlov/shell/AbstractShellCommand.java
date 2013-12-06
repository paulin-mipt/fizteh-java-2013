package ru.fizteh.fivt.students.vlmazlov.shell;

public abstract class AbstractShellCommand extends AbstractCommand<ShellState> {
    public AbstractShellCommand(String name, int argNum) {
        super(name, argNum);
    }
}
