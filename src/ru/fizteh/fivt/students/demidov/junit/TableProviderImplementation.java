package ru.fizteh.fivt.students.demidov.junit;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.demidov.basicclasses.BasicTableProvider;

public class TableProviderImplementation extends BasicTableProvider<TableImplementation> implements TableProvider {
	public TableProviderImplementation(String root) {
		super(root);
	}
	
	public TableImplementation createTable(String name) {
		if ((name == null) || (!(name.matches("\\w+")))) {
			throw new IllegalArgumentException("wrong table name " + name);
		}

		if (tables.containsKey(name)) {
			return null;
		} else {
			if (!(new File(root, name)).mkdir()) {
				throw new IllegalStateException("unable to make directory " + name);
			}
			try {
				tables.put(name, new TableImplementation(root + File.separator + name, name));
			} catch (IOException catchedException) {
				throw new IllegalStateException(catchedException);
			}
		}
		return tables.get(name);
	}

	public void readFilesMaps() throws IOException {
		for (String subdirectory : (new File(root)).list()) {
			if (!((new File(root, subdirectory)).isDirectory())) {
				throw new IOException("wrong directory " + subdirectory);
			} else {
				tables.put(subdirectory, new TableImplementation(root + File.separator + subdirectory, subdirectory));
				tables.get(subdirectory).getFilesMap().readData();
			}
		}
	}

	public void writeFilesMaps() throws IOException {
		for (String key: tables.keySet()) {
			TableImplementation table = tables.get(key);
			table.autoCommit();
			table.getFilesMap().writeData();
		}
	}

	public TableImplementation createTable(String name, List<Class<?>> columnTypes) throws IOException {
		return null;
	}
}
