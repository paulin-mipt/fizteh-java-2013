package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.shell.ShellState;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CpCommand extends AbstractCommand<ShellState> {

    public CpCommand(ShellState state) {
        super(state);
        this.name = "cp";
        this.argsCount = 2;
    }

    @Override
    public void execute(String[] args) {
        Path fromPath = Paths.get(args[0]);
        Path toPath = Paths.get(args[1]);
        try {
            state.getFileManager().copyFile(fromPath, toPath);
        }
        catch (IOException e) {
            throw new CommandExecutionException(e.getMessage());
        }
    }

}
