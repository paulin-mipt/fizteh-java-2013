package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.strings.TableProvider;
import java.util.HashMap;
import java.io.*;

public class TableDirectory implements TableProviderInterface {

	private final String dbDirectory;
	private final HashMap<String, MultiFileStorage> tables = new HashMap<String, MultiFileStorage>();
	private static final String VALID_TABLENAME_REGEXP = "[A-Za-zА-Яа-я0-9\\._-]+";

	public TableDirectory (String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	public TableInterface getTable (String name) {
		if (name == null || !name.matches(VALID_TABLENAME_REGEXP)) {
			throw new IllegalArgumentException();
		}
		File table = new File(dbDirectory, name);
		if (!table.isDirectory()) {
			return null;
		}
		try {
			MultiFileStorage savedTable = tables.get(name);
			if (savedTable == null) {
				savedTable = new MultiFileStorage(table.getCanonicalPath(), name);
				tables.put(name, savedTable);
			}
			return savedTable;
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public TableInterface createTable (String name) {
		if (name == null || !name.matches(VALID_TABLENAME_REGEXP)) {
			throw new IllegalArgumentException();
		}
		if (tables.get(name) != null) {
			return null;
		}
		File table = new File(dbDirectory, name);
		if (table.isFile()) {
			throw new IllegalArgumentException();
		}
		try {
			table.mkdir();
			MultiFileStorage savedTable = new MultiFileStorage(table.getCanonicalPath(), name);
			tables.put(name, savedTable);
			return savedTable;
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private boolean delete (String parent, String name) {
		if (name == null) {
			return true;
		}
		try {
			File file = new File(parent, name);
			String[] content = file.list();
			if (content != null) {
				for (int i = 0; i < content.length; i++) {
					if (!delete(file.getCanonicalPath(), content[i])) {
						return false;
					}
				}
			}
			return file.delete();
		}
		catch (IOException e) {}
		return false;
	}

	public void removeTable (String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException();
		}
		File table = new File(dbDirectory, name);
		if (!table.isDirectory()) {
			throw new IllegalStateException(name + " not exists");
		}
		if (!delete(dbDirectory, name)) {
			throw new IllegalArgumentException();
		}
		tables.remove(name);
	}
};