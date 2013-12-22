package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.students.inaumov.filemap.FileMapStringShellState;
import ru.fizteh.fivt.students.inaumov.multifilemap.base.DatabaseFactory;
import ru.fizteh.fivt.students.inaumov.shell.base.Command;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;
import ru.fizteh.fivt.students.inaumov.filemap.commands.*;
import ru.fizteh.fivt.students.inaumov.multifilemap.commands.*;

public class MultiFileMapMain {
    public static void main(String[] args) {
        Shell<MultiFileMapStringShellState> shell = new Shell<MultiFileMapStringShellState>();

        String databaseDir = System.getProperty("fizteh.db.dir");
        if (!MultiFileMapUtils.isCorrectDir(databaseDir)) {
            System.err.println("database directory is incorrect");
            System.exit(1);
        }

        try {
            MultiFileMapStringShellState shellState = new MultiFileMapStringShellState();
            DatabaseFactory factory = new DatabaseFactory();
            shellState.tableProvider = factory.create(databaseDir);
            shell.setState(shellState);
            shell.setArgs(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Command<FileMapStringShellState> cmd = new PutCommand();
        shell.addCommand(cmd);
        cmd = new GetCommand();
        shell.addCommand(cmd);
        cmd = new RemoveCommand();
        shell.addCommand(cmd);
        cmd = new CommitCommand();
        shell.addCommand(cmd);
        cmd = new RollbackCommand();
        shell.addCommand(cmd);
        cmd =  new ExitCommand();
        shell.addCommand(cmd);

        Command<MultiFileMapStringShellState> nextCmd = new CreateCommand();
        shell.addCommand(nextCmd);
        nextCmd = new UseCommand();
        shell.addCommand(nextCmd);
        nextCmd = new DropCommand();
        shell.addCommand(nextCmd);

        shell.run();
    }
}
