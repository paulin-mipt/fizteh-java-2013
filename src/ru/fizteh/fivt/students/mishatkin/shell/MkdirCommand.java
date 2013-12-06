package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * MkdirCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class MkdirCommand<Receiver extends ShellReceiver> extends ShellCommand<Receiver> {
	public MkdirCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(1);
	}

	@Override
	public void execute() throws ShellException {
		receiver.makeDirectoryCommand(args[0]);
	}
}
