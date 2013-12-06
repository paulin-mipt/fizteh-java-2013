package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.ShellException;

/**
 * Created by Vladimir Mishatkin on 11/2/13
 */
public class GetCommand<Receiver extends MultiFileHashMapReceiver> extends ShellCommand<Receiver> {
	public GetCommand(MultiFileHashMapReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(1);
	}
	@Override
	public void execute() throws MultiFileHashMapException {
		receiver.getCommand(args[0]);
	}
}