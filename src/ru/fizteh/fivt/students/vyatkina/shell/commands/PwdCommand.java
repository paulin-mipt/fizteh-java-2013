package ru.fizteh.fivt.students.vyatkina.shell.commands;


import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.shell.ShellState;

public class PwdCommand extends AbstractCommand<ShellState> {

    public PwdCommand(ShellState state) {
        super(state);
        this.name = "pwd";
        this.argsCount = 0;
    }

    @Override
    public void execute(String[] args) {
        String currentDirectory = state.getFileManager().getCurrentDirectoryString();
        state.printUserMessage(currentDirectory);
    }

}
