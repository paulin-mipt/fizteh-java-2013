package ru.fizteh.fivt.students.visamsonov.storage;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import java.text.ParseException;
import ru.fizteh.fivt.students.visamsonov.util.StringUtils;

public class StructuredStorage implements StructuredTableInterface {

	private final List<Class<?>> columnTypes;
	private final StructuredTableDirectory provider;
	private final TableInterface stringStorage;

	public StructuredStorage (String dir, String tableName, List<Class<?>> colTypes, StructuredTableDirectory prov) throws IOException {
		provider = prov;
		File signatureFile = new File(dir, "signature.tsv");
		if (signatureFile.isFile()) {
			Scanner scanner = new Scanner(signatureFile);
			if (!scanner.hasNextLine()) {
				throw new IOException("empty signature.tsv");
			}
			String[] types = scanner.nextLine().trim().split("\\s+");
			columnTypes = new ArrayList(types.length);
			for (int i = 0; i < types.length; i++) {
				Class<?> type = provider.getTypeByName(types[i]);
				if (type == null) {
					throw new IOException("invalid signature.tsv");
				}
				columnTypes.add(type);
			}
		}
		else if (colTypes == null || signatureFile.exists()) {
			throw new IOException();
		}
		else {
			FileOutputStream signature = new FileOutputStream(signatureFile);
			String[] names = new String[colTypes.size()];
			for (int i = 0; i < colTypes.size(); i++) {
				names[i] = provider.getNameByType(colTypes.get(i));
			}
			signature.write(StringUtils.join(names, " ").getBytes());
			signature.close();
			columnTypes = colTypes;
		}
		stringStorage = new MultiFileStorage(dir, tableName);
	}

	public int unsavedChanges () {
		return stringStorage.unsavedChanges();
	}

	public String getName () {
		return stringStorage.getName();
	}

	public Storeable get (String key) {
		try {
			return provider.deserialize(this, stringStorage.get(key));
		}
		catch (ParseException e) {}
		return null;
	}

	public Storeable put (String key, Storeable value) throws ColumnFormatException {
		if (key != null && key.matches(".*\\s.*")) {
			throw new ColumnFormatException();
		}
		try {
			return provider.deserialize(this, stringStorage.put(key, provider.serialize(this, value)));
		}
		catch (ParseException e) {
			throw new ColumnFormatException(e);
		}
	}

	public Storeable remove (String key) {
		try {
			return provider.deserialize(this, stringStorage.remove(key));
		}
		catch (ParseException e) {}
		return null;
	}

	public int size () {
		return stringStorage.size();
	}

	public int commit () throws IOException {
		return stringStorage.commit();
	}

	public int rollback () {
		return stringStorage.rollback();
	}

	public int getColumnsCount () {
		return columnTypes.size();
	}

	public Class<?> getColumnType (int columnIndex) throws IndexOutOfBoundsException {
		return columnTypes.get(columnIndex);
	}
}