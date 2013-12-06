package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.mishatkin.filemap.FileMapDatabaseException;
import ru.fizteh.fivt.students.mishatkin.filemap.FileMapReceiver;
import ru.fizteh.fivt.students.mishatkin.filemap.FileMapReceiverProtocol;
import ru.fizteh.fivt.students.mishatkin.shell.ShellException;
import ru.fizteh.fivt.students.mishatkin.shell.TimeToExitException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public class MultiFileHashMapTableReceiver implements FileMapReceiverProtocol, Table {
	private static final int TABLE_OWNING_FILES_COUNT = MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT * MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT;

	private WeakReference<MultiFileHashMapReceiver> delegate;

	private String tableName;

	private List<FileMapReceiver> tableFiles =
			new ArrayList<>(TABLE_OWNING_FILES_COUNT);

	public MultiFileHashMapTableReceiver(String tableName) {	//	Set delegate after this!
		tableFiles = new ArrayList<>(Collections.<FileMapReceiver>nCopies(TABLE_OWNING_FILES_COUNT, null));
		this.tableName = tableName;
		delegate = null;
	}

	public String getName() {
		return tableName;
	}

	public boolean isSet() {
		return !tableName.equals("");
	}

	public void reset() {
		tableName = "";
		tableFiles =  new ArrayList<>(Collections.<FileMapReceiver>nCopies(TABLE_OWNING_FILES_COUNT, null));
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setDelegate(MultiFileHashMapReceiver delegate) {
		this.delegate = new WeakReference<>(delegate);
	}

	public MultiFileHashMapReceiver getDelegate() {
		return delegate.get();
	}

	private FileMapReceiver tableForKey(String key) throws MultiFileHashMapException {
		int hashCode = key.hashCode();
		int mod = MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT;
		int directoryIndex = Math.abs(hashCode % mod);
		int fileIndex = Math.abs((hashCode / mod) % mod);
		try {
			return tableForDirectoryAndFileIndexes(directoryIndex, fileIndex, mod);
		} catch (MultiFileHashMapException e) {
			throw new MultiFileHashMapException(e.getMessage() + " key: " + key, e);
		}
	}

	private FileMapReceiver tableForDirectoryAndFileIndexes(int directoryIndex, int fileIndex, int mod) throws MultiFileHashMapException {
		int indexInFilesList = mod * directoryIndex + fileIndex;
		if (tableFiles.get(indexInFilesList) == null) {
			String directoryName = String.valueOf(directoryIndex) + ".dir";
			String fileName = String.valueOf(fileIndex) + ".dat";
			if (getDelegate() == null) {
				System.err.println("delegate");
			}
			if (getDelegate().getDbDirectoryName() == null) {
				System.err.println("dbDir");
			}
			if (tableName == null) {
				System.err.println("tableName");
			}
			File tableDirectory = new File(getDelegate().getDbDirectoryName(), tableName);
			File directory = new File(tableDirectory, directoryName);
			try {
				directory = directory.getCanonicalFile();
			} catch (IOException e) {
			}
			if (!directory.exists()) {
				if (!directory.mkdir()) {
					try {
						throw new MultiFileHashMapException("Cannot create directory: " + directory.getCanonicalPath());
					} catch (IOException e) {
					}
				}
			} else if (!directory.isDirectory()) {
				throw new MultiFileHashMapException(directoryName + ".dir file already exists and it is most certainly not a directory. OK bye.");
			}
			String pseudoFileName = tableName + File.separator + directoryName + File.separator + fileName;
			FileMapReceiver freshDictionaryFile = null;
			try {
				MultiFileHashMapTableReceiverDelegate theDelegate = getDelegate();
				freshDictionaryFile = new FileMapReceiver(theDelegate.getDbDirectoryName(), pseudoFileName,
						theDelegate.isInteractiveMode(), theDelegate.getOut());
			} catch (FileMapDatabaseException e) {
				throw new MultiFileHashMapException("Cannot access or create file for " + directoryIndex + ".dir"
						+ File.separator + fileIndex + ".dat", e);
			}
			if (!freshDictionaryFile.doHashCodesConformHash(directoryIndex, fileIndex, MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT)) {
				throw new MultiFileHashMapException("Keys in " + directoryIndex + " directory and " + fileIndex +
						" file contain some extra keys with unacceptable hash values" );
			}
			tableFiles.set(indexInFilesList, freshDictionaryFile);
		}
		return tableFiles.get(indexInFilesList);
	}

	@Override
	public String putCommand(String key, String value) throws MultiFileHashMapException {
		return tableForKey(key).putCommand(key, value);
	}

	@Override
	public String getCommand(String key) throws MultiFileHashMapException {
		return tableForKey(key).getCommand(key);
	}

	@Override
	public String removeCommand(String key) throws MultiFileHashMapException {
		return tableForKey(key).removeCommand(key);
	}

	public void writeFilesOnDrive() throws MultiFileHashMapException {
		for (FileMapReceiver everyFile : tableFiles) {
			try {
				if (everyFile != null) {
					everyFile.exitCommand();
				}
			} catch (TimeToExitException LoLJustKidding) {
			}
		}
		for (int directoryIndex = 0; directoryIndex < MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT; ++directoryIndex) {
			String directoryName = String.valueOf(directoryIndex) + ".dir";
			File directoryFile = new File(new File(getDelegate().getDbDirectoryName(), tableName), directoryName);
			File subFiles[] = directoryFile.listFiles();
			if (directoryFile.exists() && (subFiles == null || subFiles.length == 0)) {
				getDelegate().removeTableSubDirectoryWithIndex(directoryIndex);
			}
		}
	}

	//	Table methods
	@Override
	public String get(String key) {
		if (key == null || key.equals("")) {
			throw new IllegalArgumentException();
		}
		try {
			return getCommand(key);
		} catch (MultiFileHashMapException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Override
	public String put(String key, String value) {
		if (key == null || value == null || key.trim().equals("") || value.trim().equals("")) {
			throw new IllegalArgumentException();
		}
		try {
			return putCommand(key, value);
		} catch (MultiFileHashMapException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Override
	public String remove(String key) {
		if (key == null || key.equals("")) {
			throw new IllegalArgumentException();
		}
		try {
			return removeCommand(key);
		} catch (MultiFileHashMapException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Override
	public int size() {
		try {
			forceInitDataFiles();
		} catch (MultiFileHashMapException e) {
			System.err.println(e.getMessage());
		}
		int retValue = 0;
		for (FileMapReceiver possibleTableFile : tableFiles) {
			if (possibleTableFile != null) {
				retValue += possibleTableFile.size();
			}
		}
		return retValue;
	}

	private void forceInitDataFiles() throws MultiFileHashMapException {
		int mod = MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT;
		for (int directoryIndex = 0; directoryIndex < mod; ++directoryIndex) {
			for (int fileIndex = 0; fileIndex < mod; ++ fileIndex) {
				FileMapReceiver initializedDataFile = tableForDirectoryAndFileIndexes(directoryIndex, fileIndex, mod);
				int indexInFilesList = directoryIndex * mod + fileIndex;
				tableFiles.set(indexInFilesList, initializedDataFile);
			}
		}
	}

	@Override
	public int commit() {
		int retValue = 0;
		try {
			for (FileMapReceiver possibleTableFile : tableFiles) {
				if (possibleTableFile != null) {
					retValue += possibleTableFile.commit();
				}
			}
		} catch (ShellException e) {
			System.err.println(e.getMessage());
			return 0;
		}
		return retValue;
	}

	@Override
	public int rollback() {
		int retValue = 0;
		for (FileMapReceiver possibleTableFile : tableFiles) {
			if (possibleTableFile != null) {
				retValue += possibleTableFile.rollback();
			}
		}
		return retValue;
	}

	public int getUnstagedChangesCount() {
		int retValue = 0;
		for (FileMapReceiver possibleTableFile : tableFiles) {
			if (possibleTableFile != null) {
				retValue += possibleTableFile.getUnstagedChangesCount();
			}
		}
		return retValue;
	}
}
