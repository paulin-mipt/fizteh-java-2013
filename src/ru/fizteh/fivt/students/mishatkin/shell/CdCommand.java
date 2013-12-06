package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * CdCommand.java
 * Created by Vladimir Mishatkin on 9/24/13
 */
public class CdCommand<Receiver extends ShellReceiver> extends ShellCommand<Receiver>  {

	public CdCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(1);
	}

	@Override
	public void execute() throws ShellException {
		receiver.changeDirectoryCommand(args[0]);
	}
}
