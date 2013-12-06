package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.multifilehashmap.MultiFileHashMapException;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public interface FileMapReceiverProtocol {
	public String putCommand(String key, String value) throws MultiFileHashMapException;
	public String getCommand(String key) throws MultiFileHashMapException;
	public String removeCommand(String key) throws MultiFileHashMapException;
}
