package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;

public class CommandDir implements Command {
    private final String name = "dir";
    private ShellState state;

    public CommandDir(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getArgCount() {
        return 0;
    }

    public void execute(String[] args) throws IOException {

        File[] files = state.getCurrentDirectory().listFiles();
        if (files.length > 0) {
            for (File f : files) {
                if (f.getName().equals(".")) {
                    continue;
                }
                if (f.getName().equals("..")) {
                    continue;
                }
                System.out.println(f.getName());
            }
        }
    }
}
