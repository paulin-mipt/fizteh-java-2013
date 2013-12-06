package ru.fizteh.fivt.students.mishatkin.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.mishatkin.multifilehashmap.MultiFileHashMapTableReceiver;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Vladimir Mishatkin on 11/11/13
 */

public class MultiTypeFileMapTableReceiver /*extends MultiFileHashMapTableReceiver*/ implements Table {

	private WeakReference<MultiTypeStorageReceiver> provider;

	private MultiFileHashMapTableReceiver stringTable;

	// set this after instantiating a class
	private List<Class<?>> correspondingClasses;

	public MultiTypeFileMapTableReceiver(MultiFileHashMapTableReceiver stringTable) {
		this(stringTable, null);
	}

	public  MultiTypeFileMapTableReceiver(MultiFileHashMapTableReceiver stringTable, MultiTypeStorageReceiver delegate) {
		this.stringTable = stringTable;
		setProvider(delegate);
	}

	public MultiTypeStorageReceiver getProvider() {
		return provider.get();
	}

	public void setProvider(MultiTypeStorageReceiver provider) {
		this.provider = new WeakReference<>(provider);
	}

	public void setCorrespondingClasses(List<Class<?>> classes) {
		this.correspondingClasses = classes;
	}

	@Override
	public String getName() {
		return stringTable.getName();
	}

	@Override
	public Storeable get(String key) {
		try {
			String stringValue = stringTable.get(key);
			if (stringValue != null) {
				return getProvider().deserialize(this, stringValue);
			}
		} catch (ParseException e) {
//			System.err.println("ParseException while deserializing: " + stringTable.get(key));
		}
		return null;
	}

	@Override
	public Storeable put(String key, Storeable value) throws ColumnFormatException {
		for (int i = 0; i < getColumnsCount(); ++i) {
			if (correspondingClasses.get(i) != value.getColumnAt(i).getClass()) {
				throw new ColumnFormatException();
			}
		}
		Storeable oldValue = get(key);
		stringTable.put(key, getProvider().serialize(this, value));
		return oldValue;
	}

	@Override
	public Storeable remove(String key) {
		Storeable oldValue = get(key);
		stringTable.remove(key);
		return oldValue;
	}

	@Override
	public int size() {
		return stringTable.size();
	}

	@Override
	public int commit() throws IOException {
		return stringTable.commit();
	}

	@Override
	public int rollback() {
		return stringTable.rollback();
	}

	@Override
	public int getColumnsCount() {
		return correspondingClasses.size();
	}

	@Override
	public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
		if (columnIndex < 0 || columnIndex >= getColumnsCount())
			throw new IndexOutOfBoundsException(String.valueOf(columnIndex));
		return correspondingClasses.get(columnIndex);
	}
}
