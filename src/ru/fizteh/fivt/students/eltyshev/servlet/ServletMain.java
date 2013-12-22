package ru.fizteh.fivt.students.eltyshev.servlet;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.eltyshev.filemap.base.commands.*;
import ru.fizteh.fivt.students.eltyshev.multifilemap.commands.CreateCommand;
import ru.fizteh.fivt.students.eltyshev.multifilemap.commands.DropCommand;
import ru.fizteh.fivt.students.eltyshev.multifilemap.commands.UseCommand;
import ru.fizteh.fivt.students.eltyshev.servlet.shell.ServletShellState;
import ru.fizteh.fivt.students.eltyshev.servlet.shell.StartCommand;
import ru.fizteh.fivt.students.eltyshev.servlet.shell.StopCommand;
import ru.fizteh.fivt.students.eltyshev.shell.Shell;
import ru.fizteh.fivt.students.eltyshev.shell.commands.Command;
import ru.fizteh.fivt.students.eltyshev.shell.commands.ExitCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.HelpCommand;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProvider;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServletMain {
    public static void main(String[] args) {
        Shell<ServletShellState> shell = new Shell<>();
        List<Command<?>> commands = new ArrayList<>();

        commands.add(new StartCommand());
        commands.add(new StopCommand());
        commands.add(new PutCommand<Table, String, Storeable, ServletShellState>());
        commands.add(new GetCommand<Table, String, Storeable, ServletShellState>());
        commands.add(new RemoveCommand<Table, String, Storeable, ServletShellState>());
        commands.add(new CommitCommand<ServletShellState>());
        commands.add(new RollbackCommand<ServletShellState>());
        commands.add(new SizeCommand<ServletShellState>());
        commands.add(new CreateCommand<Table, String, Storeable, ServletShellState>());
        commands.add(new DropCommand<ServletShellState>());
        commands.add(new UseCommand<Table, String, Storeable, ServletShellState>());
        commands.add(new ExitCommand<ServletShellState>());
        commands.add(new HelpCommand<ServletShellState>(commands));

        shell.setCommands(commands);

        String databaseDirectory = System.getProperty("fizteh.db.dir");

        if (databaseDirectory == null) {
            System.err.println("You haven't set database directory");
            System.exit(1);
        }

        try {
            DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
            DatabaseTableProvider provider = (DatabaseTableProvider) factory.create(databaseDirectory);
            ServletShellState shellState = new ServletShellState(provider);
            shell.setShellState(shellState);
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        shell.start();
    }
}
