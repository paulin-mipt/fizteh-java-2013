package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;

/**
 * Created by Vladimir Mishatkin on 11/2/13
 */
public class PutCommand<Receiver extends MultiFileHashMapReceiver> extends ShellCommand<Receiver> {
	public PutCommand(MultiFileHashMapReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(2);
	}
	@Override
	public void execute() throws MultiFileHashMapException {
		receiver.putCommand(args[0], args[1]);
	}
}
