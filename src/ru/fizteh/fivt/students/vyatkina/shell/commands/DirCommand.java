package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.shell.ShellState;

public class DirCommand extends AbstractCommand<ShellState> {

    public DirCommand(ShellState state) {
        super(state);
        this.name = "dir";
        this.argsCount = 0;
    }

    @Override
    public void execute(String[] args) {
        String[] files = state.getFileManager().getSortedCurrentDirectoryFiles();
        for (String file : files) {
            state.printUserMessage(file);
        }
    }
}

