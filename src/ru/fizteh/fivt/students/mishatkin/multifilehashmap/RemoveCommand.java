package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;

/**
 * Created by Vladimir Mishatkin on 11/3/13
 */
public class RemoveCommand<Receiver extends MultiFileHashMapReceiver> extends ShellCommand<Receiver> {
	public RemoveCommand(MultiFileHashMapReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(1);
	}
	@Override
	public void execute() throws MultiFileHashMapException {
		receiver.removeCommand(args[0]);
	}
}