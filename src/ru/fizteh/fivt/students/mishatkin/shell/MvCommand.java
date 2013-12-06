package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * MvCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class MvCommand<Receiver extends ShellReceiver> extends ShellCommand<Receiver> {
	public MvCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(2);
	}

	@Override
	public void execute() throws ShellException {
		receiver.moveCommand(args[0], args[1]);
	}
}
