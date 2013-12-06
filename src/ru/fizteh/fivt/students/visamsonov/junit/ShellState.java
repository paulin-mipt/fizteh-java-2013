package ru.fizteh.fivt.students.visamsonov.junit;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.visamsonov.storage.*;
import java.io.*;

public class ShellState {

	public TableInterface database;
	public final TableProviderInterface tableProvider;

	public ShellState () throws IOException {
		TableProviderFactoryInterface factory = new TableFactory();
		String directory = System.getProperty("fizteh.db.dir");
		try {
			tableProvider = factory.create(directory);
		}
		catch (IllegalArgumentException e) {
			throw new IOException("invalid dir provided", e);
		}
	}
}