package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * CpCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class CpCommand<Receiver extends ShellReceiver> extends ShellCommand<Receiver> {
	public CpCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(2);
	}

	@Override
	public void execute() throws ShellException {
		receiver.copyCommand(args[0], args[1]);
	}
}
