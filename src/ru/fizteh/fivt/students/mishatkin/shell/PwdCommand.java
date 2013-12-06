package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * PwdCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class PwdCommand<Receiver extends ShellReceiver> extends ShellCommand<Receiver> {
	public PwdCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(0);
	}

	@Override
	public void execute() throws ShellException {
		receiver.printWorkingDirectoryCommand();
	}
}
