package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.students.inaumov.multifilemap.base.DatabaseFactory;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;
import ru.fizteh.fivt.students.inaumov.filemap.commands.*;
import ru.fizteh.fivt.students.inaumov.multifilemap.commands.*;

public class MultiFileMapMain {
    public static void main(String[] args) {
        String databaseDir = System.getProperty("fizteh.db.dir");
        if (!MultiFileMapUtils.isCorrectDir(databaseDir)) {
            System.err.println("database directory is incorrect");
            System.exit(1);
        }

        Shell<MultiFileMapShellState> shell = new Shell<MultiFileMapShellState>();

        MultiFileMapShellState shellState = new MultiFileMapShellState();
        DatabaseFactory factory = new DatabaseFactory();
        shellState.tableProvider = factory.create(databaseDir);

        shell.setState(shellState);
        shell.setArgs(args);

        shell.addCommand(new PutCommand());
        shell.addCommand(new GetCommand());
        shell.addCommand(new RemoveCommand());
        shell.addCommand(new SizeCommand());
        shell.addCommand(new CommitCommand());
        shell.addCommand(new RollbackCommand());
        shell.addCommand(new CreateCommand());
        shell.addCommand(new DropCommand());
        shell.addCommand(new UseCommand());
        shell.addCommand(new ExitCommand());

        shell.run();
    }
}
