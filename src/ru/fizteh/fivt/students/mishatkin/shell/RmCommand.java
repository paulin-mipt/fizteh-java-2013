package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * RmCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class RmCommand<Receiver extends ShellReceiver> extends ShellCommand<Receiver> {
	public RmCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(1);
	}

	@Override
	public void execute() throws ShellException {
		receiver.rmCommand(args[0]);
	}
}
