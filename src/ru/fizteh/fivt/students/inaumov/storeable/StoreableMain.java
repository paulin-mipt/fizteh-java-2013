package ru.fizteh.fivt.students.inaumov.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.inaumov.filemap.commands.*;
import ru.fizteh.fivt.students.inaumov.multifilemap.commands.*;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProviderFactory;

import java.io.IOException;

public class StoreableMain {
    public static void main(String[] args) {
        Shell<StoreableShellState> shell = new Shell<StoreableShellState>();
        shell.addCommand(new PutCommand<Table, String, Storeable, StoreableShellState>());
        shell.addCommand(new GetCommand<Table, String, Storeable, StoreableShellState>());
        shell.addCommand(new RemoveCommand<Table, String, Storeable, StoreableShellState>());
        shell.addCommand(new CommitCommand<StoreableShellState>());
        shell.addCommand(new RollbackCommand<StoreableShellState>());
        shell.addCommand(new SizeCommand<StoreableShellState>());
        shell.addCommand(new CreateCommand<Table, String, Storeable, StoreableShellState>());
        shell.addCommand(new UseCommand<Table, String, Storeable, StoreableShellState>());
        shell.addCommand(new DropCommand<StoreableShellState>());
        shell.addCommand(new ExitCommand<StoreableShellState>());

        shell.setArgs(args);

        String databaseDirectory = System.getProperty("fizteh.db.dir");
        if (databaseDirectory == null || databaseDirectory.trim().isEmpty()) {
            System.err.println("error: empty database directory");
            System.exit(1);
        }

        DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
        try {
            StoreableShellState shellState = new StoreableShellState(factory.create(databaseDirectory));
            shell.setState(shellState);
        } catch (IOException e) {
            System.err.println("error: can't load table");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        shell.run();
    }
}
