package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.mishatkin.filemap.FileMapReceiverProtocol;
import ru.fizteh.fivt.students.mishatkin.shell.ShellException;
import ru.fizteh.fivt.students.mishatkin.shell.ShellPrintStream;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;
import ru.fizteh.fivt.students.mishatkin.shell.TimeToExitException;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public class MultiFileHashMapReceiver extends ShellReceiver
		implements FileMapReceiverProtocol, MultiFileHashMapReceiverProtocol, MultiFileHashMapTableReceiverDelegate, TableProvider {

	private String dbDirectory;

	Map<String, MultiFileHashMapTableReceiver> allTables = new HashMap<>();

	private MultiFileHashMapTableReceiver table;	//	the one that is in use

	public MultiFileHashMapReceiver(PrintStream out, boolean interactiveMode, String dbDirectory) {
		super(out, interactiveMode);
		this.dbDirectory = dbDirectory;
		this.table = null;
		initAllTables();
	}

	public MultiFileHashMapReceiver(String dbDirectory) {
		this(null, false, dbDirectory);
	}

	void initAllTables() {
		File subFiles[] = new File(dbDirectory).listFiles();
		if (subFiles != null) {
			for (File subFile :subFiles) {
				if (subFile.isDirectory()) {
					String existingTableName = subFile.getName();
					MultiFileHashMapTableReceiver existingTable = new MultiFileHashMapTableReceiver(existingTableName);
					existingTable.setDelegate(this);
					existingTable.setTableName(existingTableName);
					allTables.put(existingTableName, existingTable);
				}
			}
		}
	}

	@Override
	public void createCommand(String tableName) throws MultiFileHashMapException {
		File tableFile = new File(new File(dbDirectory), tableName);
		if (tableFile.exists()) {
			if (tableFile.isDirectory()) {
				println(tableName + " exists");
			} else {
				throw new MultiFileHashMapException("It\'s a trap! Trying to create \'" + tableName +
						"\' table, but it is already a file and not a directory!");
			}
		} else {
			tableFile.mkdir();
			MultiFileHashMapTableReceiver newTable = new MultiFileHashMapTableReceiver(tableName);
			newTable.setDelegate(this);
			newTable.setTableName(tableName);
			allTables.put(tableName, newTable);
			println("created");
		}
	}

	@Override
	public void dropCommand(String tableName) throws MultiFileHashMapException {
		File dbDirectoryFile = new File(dbDirectory);
		File tableFile = new File(dbDirectoryFile, tableName);
		if (tableFile.exists()) {
			if (tableFile.isDirectory()) {
				try {
					rmCommand(new File(dbDirectoryFile, tableName).getAbsolutePath());
				} catch (ShellException e) {
					throw new MultiFileHashMapException(e.getMessage());
				}
				println("dropped");
				allTables.remove(tableName);
				if (table != null && tableName.equals(table.getName())) {
					// it is in use
					// so should reset it
					table = null;
				}
			} else {
				throw new MultiFileHashMapException("It\'s a trap! Trying to drop \'" + tableName +
						"\' table, but it is not even a directory!");
			}
		} else {
			//	conforming the protocol here
			println(tableName + " not exists");	//	I is more stronger than dart vapour
		}
	}

	@Override
	public void useCommand(String tableName) throws MultiFileHashMapException {
		File tableFile = new File(new File(dbDirectory), tableName);
		if (tableFile.exists()) {
			if (tableFile.isDirectory()) {
				//Save first
				if (table != null) {
					table.writeFilesOnDrive();
				}
				//Use the force, Harry! (c) Handalf
				table = allTables.get(tableName);
				println("using " + tableName);
			} else {
				throw new MultiFileHashMapException("It\'s a trap! Trying to use \'" + tableName +
						"\' table, which is clearly not a directory!");
			}
		} else {
			println(tableName + " not exists");
		}
	}

	@Override
	public String putCommand(String key, String value) throws MultiFileHashMapException {
		if (table != null) {
			return table.putCommand(key, value);
		} else {
			println("no table");
		}
		return null;
	}

	@Override
	public String getCommand(String key) throws MultiFileHashMapException {
		if (table.isSet()) {
			return table.getCommand(key);
		} else {
			println("no table");
		}
		return null;
	}

	@Override
	public String removeCommand(String key) throws MultiFileHashMapException {
		if (table.isSet()) {
			return table.removeCommand(key);
		} else {
			println("no table");
		}
		return null;
	}

	@Override
	public void exitCommand() throws TimeToExitException {
		try {
			table.writeFilesOnDrive();
		} catch (MultiFileHashMapException e) {
			System.err.println(e.getMessage());
		}
		super.exitCommand();
	}
//	Delegate methods
	@Override
	public String getDbDirectoryName() {
		return dbDirectory;
	}

	@Override
	public ShellPrintStream getOut() {
		return super.getOut();
	}

	public void removeTableSubDirectoryWithIndex(int directoryIndex) throws MultiFileHashMapException {
		String directoryRelativeName = table.getName() + File.separator + String.valueOf(directoryIndex) + ".dir";
		try {
			rmCommand(dbDirectory + File.separator + directoryRelativeName);
		} catch (ShellException e) {
			throw new MultiFileHashMapException("Internal error: cannot remove directory: " + directoryRelativeName, e);
		}

	}

//	TableProvider methods
	@Override
	public Table getTable(String name) {
		return allTables.get(name);
	}

	@Override
	public Table createTable(String name) {
		try {
			createCommand(name);
		} catch (MultiFileHashMapException e) {
			System.err.println(e.getMessage());
		}
		return getTable(name);
	}

	@Override
	public void removeTable(String name) {
		try {
			dropCommand(name);
		} catch (MultiFileHashMapException e) {
			System.err.println(e);
		}
	}
}
