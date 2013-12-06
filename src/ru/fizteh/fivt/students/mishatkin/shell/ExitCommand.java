package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * ExitCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class ExitCommand<Receiver extends ShellReceiver> extends ShellCommand<Receiver> {
	public ExitCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(0);
	}

	@Override
	public void execute() throws ShellException {
		receiver.exitCommand();
	}
}
