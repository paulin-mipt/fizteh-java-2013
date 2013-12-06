package ru.fizteh.fivt.students.mishatkin.junit;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.ShellException;

/**
 * Created by Vladimir Mishatkin on 11/4/13
 */
public class RollbackCommand<Receiver extends JUnitReceiver> extends ShellCommand<Receiver> {
	public RollbackCommand(JUnitReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(0);
	}

	@Override
	public void execute() throws ShellException {
		receiver.rollbackCommand();
	}
}
