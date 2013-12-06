package ru.fizteh.fivt.students.demidov.junit;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.filemap.Get;
import ru.fizteh.fivt.students.demidov.filemap.Put;
import ru.fizteh.fivt.students.demidov.filemap.Remove;
import ru.fizteh.fivt.students.demidov.multifilehashmap.Create;
import ru.fizteh.fivt.students.demidov.multifilehashmap.Drop;
import ru.fizteh.fivt.students.demidov.multifilehashmap.Use;
import ru.fizteh.fivt.students.demidov.shell.Exit;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Main {
	public static void main(String[] arguments) {
		TableProviderImplementation provider = null;
		try {
			provider = (new TableProviderFactoryImplementation()).create(System.getProperty("fizteh.db.dir"));
		} catch (IllegalArgumentException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
		
		DataBaseState state = new DataBaseState(provider);
		
		Shell usedShell = new Shell(System.getProperty("user.dir"), System.in, System.out);
		usedShell.curShell.loadCommand(new Get(state));
		usedShell.curShell.loadCommand(new Put(state));
		usedShell.curShell.loadCommand(new Remove(state));
		usedShell.curShell.loadCommand(new Create<String, TableImplementation>(state));
		usedShell.curShell.loadCommand(new Drop<String, TableImplementation>(state));
		usedShell.curShell.loadCommand(new Use<String, TableImplementation>(state));
		usedShell.curShell.loadCommand(new Commit<String, TableImplementation>(state));
		usedShell.curShell.loadCommand(new Size<String, TableImplementation>(state));
		usedShell.curShell.loadCommand(new Rollback<String, TableImplementation>(state));
		usedShell.curShell.loadCommand(new Exit());
		
		try {
			provider.readFilesMaps();
			usedShell.startShell(arguments);
			provider.writeFilesMaps();
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
	}
}
