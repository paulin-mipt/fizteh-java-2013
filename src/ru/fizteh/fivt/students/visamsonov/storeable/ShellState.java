package ru.fizteh.fivt.students.visamsonov.storeable;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.visamsonov.storage.*;
import java.io.*;

public class ShellState {

	public StructuredTableInterface database;
	public final StructuredTableProviderInterface tableProvider;

	public ShellState () throws IOException {
		StructuredTableProviderFactoryInterface factory = new StructuredTableFactory();
		String directory = System.getProperty("fizteh.db.dir");
		try {
			tableProvider = factory.create(directory);
		}
		catch (IllegalArgumentException e) {
			throw new IOException("invalid dir provided", e);
		}
	}
}