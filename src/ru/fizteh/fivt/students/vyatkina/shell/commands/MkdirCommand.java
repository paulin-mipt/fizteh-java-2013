package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.shell.ShellState;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MkdirCommand extends AbstractCommand<ShellState> {

    public MkdirCommand(ShellState state) {
        super(state);
        this.name = "mkdir";
        this.argsCount = 1;
    }

    @Override
    public void execute(String[] args) {
        Path newDir = Paths.get(args[0]);
        try {
            state.getFileManager().makeDirectory(newDir);
        }
        catch (IOException e) {
            throw new CommandExecutionException(e.getMessage());
        }

    }
}
