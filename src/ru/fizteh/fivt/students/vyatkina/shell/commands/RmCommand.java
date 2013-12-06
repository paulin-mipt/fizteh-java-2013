package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.shell.ShellState;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RmCommand extends AbstractCommand<ShellState> {

    public RmCommand(ShellState state) {
        super(state);
        this.name = "rm";
        this.argsCount = 1;
    }

    @Override
    public void execute(String[] args) {
        try {
            Path file = Paths.get(args[0]);
            state.getFileManager().deleteFile(file);
        }
        catch (IOException e) {
            throw new CommandExecutionException(e.getMessage());
        }
    }
}
