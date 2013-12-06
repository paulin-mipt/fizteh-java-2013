package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.IOException;
import java.io.OutputStream;

public class PwdCommand extends AbstractShellCommand {
    public PwdCommand() {
        super("pwd", 0);
    }

    ;

    public void execute(String[] args, ShellState state, OutputStream out) throws CommandFailException {
        try {
            out.write(state.getCurDir().getBytes());
            out.write('\n');
        } catch (IOException ex) {
            throw new CommandFailException("pwd: Unable to print current directory");
        }
    }
}
