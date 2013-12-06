package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public interface MultiFileHashMapReceiverProtocol {
	public void createCommand(String tableName) throws MultiFileHashMapException;
	public boolean dropCommand(String tableName) throws MultiFileHashMapException;
	public void useCommand(String tableName) throws MultiFileHashMapException;
}
