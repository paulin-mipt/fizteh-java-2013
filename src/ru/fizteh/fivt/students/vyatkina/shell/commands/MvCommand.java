package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.shell.ShellState;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MvCommand extends AbstractCommand<ShellState> {

    public MvCommand(ShellState state) {
        super(state);
        this.name = "mv";
        this.argsCount = 2;
    }

    @Override
    public void execute(String[] args) {
        try {
            Path fromPath = Paths.get(args[0]);
            Path toPath = Paths.get(args[1]);
            state.getFileManager().moveFile(fromPath, toPath);
        }
        catch (IOException e) {
            throw new CommandExecutionException(e.getMessage());
        }
    }

}
