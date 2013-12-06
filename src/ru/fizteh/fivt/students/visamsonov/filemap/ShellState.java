package ru.fizteh.fivt.students.visamsonov.filemap;

import ru.fizteh.fivt.students.visamsonov.storage.*;
import ru.fizteh.fivt.storage.strings.Table;
import java.io.*;
import java.util.TreeMap;

public class ShellState {

	public Table database;

	public ShellState () throws FileNotFoundException, IOException {
		database = new FileStorage(System.getProperty("fizteh.db.dir"), "db.dat");
	}
}