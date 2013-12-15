package ru.fizteh.fivt.students.inaumov.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;
import ru.fizteh.fivt.students.inaumov.filemap.commands.*;

public class Main {
    public static void main(String[] args) {
        Shell<FileMapStringShellState> shell = new Shell<FileMapStringShellState>();
        FileMapStringShellState shellState = new FileMapStringShellState();

        String databaseDir = System.getProperty("fizteh.db.dir");
        if (databaseDir == null || databaseDir.isEmpty()) {
            System.err.println("choose working directory");
            System.exit(1);
        }

        shellState.table = new SingleFileStringDatabaseTable(databaseDir, "database");

        shell.setState(shellState);
        shell.setArgs(args);

        shell.addCommand(new PutCommand<Table, String, String, FileMapStringShellState>());
        shell.addCommand(new GetCommand<Table, String, String, FileMapStringShellState>());
        shell.addCommand(new RemoveCommand<Table, String, String, FileMapStringShellState>());
        shell.addCommand(new CommitCommand<FileMapStringShellState>());
        shell.addCommand(new RollbackCommand<FileMapStringShellState>());
        shell.addCommand(new ExitCommand());

        shell.run();
    }
}
