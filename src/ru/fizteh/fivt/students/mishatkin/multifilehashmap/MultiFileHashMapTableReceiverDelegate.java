package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellPrintStream;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public interface MultiFileHashMapTableReceiverDelegate {
	public String getDbDirectoryName();
	public ShellPrintStream getOut();//lol
	public boolean isInteractiveMode();
}
