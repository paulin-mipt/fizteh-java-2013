package ru.fizteh.fivt.students.demidov.basicclasses;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.fizteh.fivt.students.demidov.shell.Utils;

abstract public class BasicTableProvider<TableType> {
	protected Map<String, TableType> tables;
	protected ReadWriteLock providerLock;
	protected String root;
	
	public BasicTableProvider(String root) {
		tables = new HashMap<String, TableType>();
		providerLock = new ReentrantReadWriteLock();

		this.root = root;                
		if (!((new File(root)).isDirectory())) {
			throw new IllegalArgumentException("wrong directory: " + root);
		}
	}
	
	public TableType getTable(String name) {
		if ((name == null) || (!(name.matches("\\w+")))) {
			throw new IllegalArgumentException("wrong table name: " + name);
		}
		
		providerLock.readLock().lock();		
		try {
			return tables.get(name);
		} finally {		
			providerLock.readLock().unlock();
		}
	}
	
	public void removeTable(String name) {
		if ((name == null) || (!(name.matches("\\w+")))) {
			throw new IllegalArgumentException("wrong table name: " + name);
		}
		
		providerLock.writeLock().lock();
		
		try {
		    if (!(tables.containsKey(name))) {
		        throw new IllegalStateException(name + " not exists");
		    }
		    tables.remove(name);
		    Utils.deleteFileOrDirectory(new File(root, name));
		} finally {		
		    providerLock.writeLock().unlock();
		}
	}
	
	abstract public TableType createTable(String name);
	abstract public TableType createTable(String name, List<Class<?>> columnTypes) throws IOException;
}
