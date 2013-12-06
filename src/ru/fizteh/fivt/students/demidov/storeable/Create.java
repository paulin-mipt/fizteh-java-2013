package ru.fizteh.fivt.students.demidov.storeable;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.demidov.basicclasses.BasicDataBaseState;
import ru.fizteh.fivt.students.demidov.multifilehashmap.BasicMultiFileHashMapCommand;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Create extends BasicMultiFileHashMapCommand<Storeable, StoreableTable> {
	public Create(BasicDataBaseState<Storeable, StoreableTable> dataBaseState) {
		super(dataBaseState, "create", 2);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException { 
		try {
			dataBaseState.create(arguments[0], StoreableUtils.parseArguments(arguments[1].split("\\s+")));
		} catch (IllegalStateException catchedException) {
			throw new IOException(catchedException.getMessage());
		} catch (IllegalArgumentException catchedException) {
			throw new IOException("wrong type (" + catchedException.getMessage() + ")");
		} catch (IOException catchedException) {
			throw new IOException("wrong type (" + catchedException.getMessage() + ")");
		}
		usedShell.curShell.getOutStream().println("created");
	}	
}
