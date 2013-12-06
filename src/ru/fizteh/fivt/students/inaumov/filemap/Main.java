package ru.fizteh.fivt.students.inaumov.filemap;

import ru.fizteh.fivt.students.inaumov.filemap.base.SingleFileTable;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;
import ru.fizteh.fivt.students.inaumov.filemap.commands.*;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
        String databaseDir = System.getProperty("fizteh.db.dir");
        if (databaseDir == null) {
            System.err.println("choose working directory");
            System.exit(1);
        }

		Shell<SingleFileMapShellState> fileMapShell = new Shell<SingleFileMapShellState>();

		SingleFileMapShellState shellState = new SingleFileMapShellState();
        try {
			shellState.table = new SingleFileTable(databaseDir, "database");
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}  catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

		fileMapShell.setState(shellState);
        fileMapShell.setArgs(args);

		fileMapShell.addCommand(new PutCommand());
		fileMapShell.addCommand(new GetCommand());
		fileMapShell.addCommand(new RemoveCommand());
		fileMapShell.addCommand(new ExitCommand());
		fileMapShell.addCommand(new CommitCommand());
		fileMapShell.addCommand(new RollbackCommand());
		fileMapShell.addCommand(new SizeCommand());

        fileMapShell.run();
	}
}
