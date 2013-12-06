package ru.fizteh.fivt.students.visamsonov.filehashmap;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.visamsonov.storage.TableFactory;
import java.io.*;

public class ShellState {

	public Table database;
	public final TableProvider tableProvider;

	public ShellState () throws IOException {
		TableProviderFactory factory = new TableFactory();
		String directory = System.getProperty("fizteh.db.dir");
		if (!(new File(directory).isDirectory())) {
			throw new IOException("no such directory: \"" + directory + "\"");
		}
		tableProvider = factory.create(directory);
	}
}