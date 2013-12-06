package ru.fizteh.fivt.students.demidov.storeable;

import java.io.IOException;
import java.util.List;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.demidov.basicclasses.BasicDataBaseState;

public class StoreableState extends BasicDataBaseState<Storeable, StoreableTable> {
	public StoreableState(StoreableTableProvider provider) {
		super(provider);
	}
	
	public void use(String tableName) throws IOException {
		if (usedTable != null) {
			int changesNumber = usedTable.getChangesNumber();
			if (changesNumber > 0) {
				throw new IOException(changesNumber + " unsaved changes");
			}
		}
		StoreableTable gotTable = null;
		try {
			gotTable = provider.getTable(tableName);
		} catch (IllegalArgumentException catchedException) {
			throw new IOException(catchedException);
		}
		if (gotTable == null) {
			throw new IOException(tableName + " not exists");
		}
		usedTable = gotTable;	
	}

	public void create(String tableName, List<Class<?>> columnTypes) throws IOException {
		StoreableTable createdTable = null;
		try {
			createdTable = provider.createTable(tableName, columnTypes);		
		} catch (IllegalArgumentException catchedException) {
			throw new IOException(catchedException);
		} catch (IllegalStateException catchedException) {
			throw new IOException(catchedException);
		}
		if (createdTable == null) {
			throw new IllegalStateException(tableName + " exists");
		}
	}
	
	public String put(String key, String value) throws IOException {
		StoreableTable table = getUsedTable();
		return table.serialize(table.put(key, table.deserialize(value)));
	}

	public String remove(String key) throws IOException {
		StoreableTable table = getUsedTable();
		return table.serialize(table.remove(key));
	}


	public String get(String key) throws IOException {
		StoreableTable table = getUsedTable();
		return table.serialize(table.get(key));
	}
	
	public void create(String tableName) throws IOException {}
}
