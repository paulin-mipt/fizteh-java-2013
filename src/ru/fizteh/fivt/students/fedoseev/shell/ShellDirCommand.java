package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.IOException;

public class ShellDirCommand extends AbstractCommand<ShellState> {
    public ShellDirCommand() {
        super("dir", 0);
    }

    @Override
    public void execute(String[] input, ShellState state) throws IOException {
        File curDir = new File(state.getCurState().toString());
        File[] files = curDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!file.isHidden()) {
                    System.out.println(file.getName());
                }
            }
        }
    }
}
