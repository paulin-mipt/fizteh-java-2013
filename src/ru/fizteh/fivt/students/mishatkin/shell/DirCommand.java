package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * DirCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class DirCommand<Receiver extends ShellReceiver> extends ShellCommand<Receiver> {
	public DirCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(0);
	}

	@Override
	public void execute() throws ShellException {
		receiver.directoryCommand();
	}

}
