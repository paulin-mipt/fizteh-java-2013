package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class DirCommand extends AbstractShellCommand {
    public DirCommand() {
        super("dir", 0);
    }

    ;

    public void execute(String[] args, ShellState state, OutputStream out) throws CommandFailException {
        File curDir = new File(state.getCurDir());
        String[] listing = curDir.list();

        if (listing.length == 0) {
            return;
        }

        try {
            out.write((StringUtils.join(Arrays.asList(listing), System.getProperty("line.separator"))).getBytes());
            out.write('\n');
        } catch (IOException ex) {
            throw new CommandFailException("dir: Unable to print listing");
        }
    }
}
