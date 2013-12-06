package ru.fizteh.fivt.students.mishatkin.junit;

import ru.fizteh.fivt.students.mishatkin.multifilehashmap.MultiFileHashMapException;
import ru.fizteh.fivt.students.mishatkin.multifilehashmap.MultiFileHashMapReceiver;

import java.io.PrintStream;

/**
 * Created by Vladimir Mishatkin on 11/4/13
 */
public class JUnitReceiver extends MultiFileHashMapReceiver {
	public JUnitReceiver(PrintStream out, boolean interactiveMode, String dbDirectory) {
		super(out, interactiveMode, dbDirectory);
	}

	public JUnitReceiver(String dbDirectory) {
		super(dbDirectory);
	}

	public int sizeCommand() {
		int retValue = table != null ? table.size() : 0;
		println(String.valueOf(retValue));
		return retValue;
	}

	public int commitCommand() {
		int retValue = table.commit();
		println(String.valueOf(retValue));
		return retValue;
	}

	public int rollbackCommand() {
		int retValue = table.rollback();
		println(String.valueOf(retValue));
		return retValue;
	}

	private boolean warnIfUnstagedChangesExist() throws MultiFileHashMapException {
		int unstagedChangesCount = 0;
		if (table != null) {
			unstagedChangesCount = table.getUnstagedChangesCount();
		}
		if (unstagedChangesCount > 0) {
			println(unstagedChangesCount + " unsaved changes");
			return true;
		}
		return false;
	}

	@Override
	public void useCommand(String tableName) throws MultiFileHashMapException {
		if (!warnIfUnstagedChangesExist()) {
			super.useCommand(tableName);
		}
	}
}
