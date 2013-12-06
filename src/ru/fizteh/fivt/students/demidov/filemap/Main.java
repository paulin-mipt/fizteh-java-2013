package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.shell.Exit;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Main {
	public static void main(String[] arguments) {
		FileMap<String> fileMap = null;
		String path = System.getProperty("fizteh.db.dir");
		if (path == null) {
			System.err.println("null path");
			System.exit(1);
		}
		
		FileMapTable fileMapTable = null;
		try {
			fileMapTable = new FileMapTable(path, "defaultTable");	
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
		
		fileMap = new FileMap<String>(-1, -1, path, fileMapTable);
		FileMapState state = new FileMapState(fileMap);

		Shell usedShell = new Shell(System.getProperty("user.dir"), System.in, System.out);
		usedShell.curShell.loadCommand(new Get(state));
		usedShell.curShell.loadCommand(new Put(state));
		usedShell.curShell.loadCommand(new Remove(state));
		usedShell.curShell.loadCommand(new Exit());

		try {
			fileMap.readDataFromFile();
			usedShell.startShell(arguments);
			fileMap.writeDataToFile();
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		}
	}
}
