package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.shell.ShellState;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CdCommand extends AbstractCommand<ShellState> {

    public CdCommand(ShellState state) {
        super(state);
        this.name = "cd";
        this.argsCount = 1;
    }

    @Override
    public void execute(String[] args) {
        Path newDirectory = Paths.get(args[0]);
        try {
            state.getFileManager().changeCurrentDirectory(newDirectory);
        }
        catch (IOException | IllegalArgumentException e) {
            throw new CommandExecutionException(e.getMessage());
        }
    }

}
