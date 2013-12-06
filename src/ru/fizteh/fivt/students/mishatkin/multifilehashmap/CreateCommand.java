package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.ShellException;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public class CreateCommand<Receiver extends MultiFileHashMapReceiver> extends ShellCommand<Receiver> {
	public CreateCommand(MultiFileHashMapReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(1);
	}

	@Override
	public void execute() throws ShellException {
		receiver.createCommand(args[0]);
	}
}
