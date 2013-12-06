package ru.fizteh.fivt.students.demidov.basicclasses;

import java.io.IOException;
import java.util.List;

public abstract class BasicDataBaseState<ElementType, TableType extends BasicTable<ElementType>> implements BasicState {
	protected BasicTableProvider<TableType> provider;	
	protected TableType usedTable;
	
	public BasicDataBaseState(BasicTableProvider<TableType> provider) {
		this.provider = provider;
		usedTable = null;
	}
	
	public TableType getUsedTable() throws IOException {
		if (usedTable == null) {
			throw new IOException("no table");
		}
		return usedTable;
	}
	
	public void drop(String tableName) throws IOException {
		if ((usedTable != null) && (usedTable.getName().equals(tableName))) {
			usedTable = null;
		}
		try {
			provider.removeTable(tableName);
		} catch (IllegalArgumentException catchedException) {
			throw new IOException(catchedException.getMessage());
		} catch (IllegalStateException catchedException) {
			throw new IOException(catchedException.getMessage());
		}
	}
	
	abstract public void use(String tableName) throws IOException;	
	abstract public void create(String name) throws IOException;
	abstract public void create(String name, List<Class<?>> columnTypes) throws IOException;	
	abstract public String get(String key) throws IOException;
	abstract public String put(String key, String value) throws IOException;	
	abstract public String remove(String key) throws IOException;
}
