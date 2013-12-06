package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class ShellPwdCommand extends AbstractCommand<ShellState> {
    public ShellPwdCommand() {
        super("pwd", 0);
    }

    @Override
    public void execute(String[] input, ShellState state) throws IOException {
        System.out.println(state.getCurState());
    }
}
