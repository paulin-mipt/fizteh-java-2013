package ru.fizteh.fivt.students.visamsonov.storage;

import java.io.*;

public class MultiFileStorage implements TableInterface {

	private final int NUMBER_OF_DIRS = 16;
	private final int NUMBER_OF_FILES = 16;
	private final String directory;
	private final String tableName;
	private final TableInterface[][] database;

	public MultiFileStorage (String directory, String tableName) throws IOException {
		this.directory = directory;
		this.tableName = tableName;
		database = new FileStorage[NUMBER_OF_DIRS][NUMBER_OF_FILES];
		for (int dirNumber = 0; dirNumber < NUMBER_OF_DIRS; dirNumber++) {
			File subdirectory = new File(directory, dirNumber + ".dir");
			for (int fileNumber = 0; fileNumber < NUMBER_OF_FILES; fileNumber++) {
				database[dirNumber][fileNumber] = new FileStorage(subdirectory.getAbsolutePath(), fileNumber + ".dat");
			}
		}
	}

	private TableInterface getAppropriate (String key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException();
		}
		int randomData = Math.abs(key.getBytes()[0]);
		int dirNumber = randomData % NUMBER_OF_DIRS;
		int nfile = (randomData / NUMBER_OF_DIRS) % NUMBER_OF_FILES;
		File subdirectory = new File(directory, dirNumber + ".dir");
		subdirectory.mkdir();
		return database[dirNumber][nfile];
	}

	public String getName () {
		return tableName;
	}

	public String get (String key) {
		return getAppropriate(key).get(key);
	}

	public String put (String key, String value) {
		return getAppropriate(key).put(key, value);
	}

	public String remove (String key) {
		return getAppropriate(key).remove(key);
	}

	public int rollback () {
		int result = 0;
		for (int dirNumber = 0; dirNumber < NUMBER_OF_DIRS; dirNumber++) {
			for (int fileNumber = 0; fileNumber < NUMBER_OF_FILES; fileNumber++) {
				result += database[dirNumber][fileNumber].rollback();
			}
		}
		return result;
	}

	public int size () {
		int totalSize = 0;
		for (int dirNumber = 0; dirNumber < NUMBER_OF_DIRS; dirNumber++) {
			for (int fileNumber = 0; fileNumber < NUMBER_OF_FILES; fileNumber++) {
				totalSize += database[dirNumber][fileNumber].size();
			}
		}
		return totalSize;
	}

	public int unsavedChanges () {
		int totalSize = 0;
		for (int dirNumber = 0; dirNumber < NUMBER_OF_DIRS; dirNumber++) {
			for (int fileNumber = 0; fileNumber < NUMBER_OF_FILES; fileNumber++) {
				totalSize += database[dirNumber][fileNumber].unsavedChanges();
			}
		}
		return totalSize;
	}

	public int commit () {
		int result = 0;
		for (int dirNumber = 0; dirNumber < NUMBER_OF_DIRS; dirNumber++) {
			File subdirectory = new File(directory, dirNumber + ".dir");
			for (int fileNumber = 0; fileNumber < NUMBER_OF_FILES; fileNumber++) {
				result += database[dirNumber][fileNumber].commit();
			}
			String[] list = subdirectory.list();
			if (list != null && list.length == 0) {
				subdirectory.delete();
			}
		}
		return result;
	}
}