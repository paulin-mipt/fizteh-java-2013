package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.TimeToExitException;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class GetCommand<Receiver extends FileMapReceiver> extends ShellCommand<Receiver> {
	public GetCommand(FileMapReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(1);
	}
	@Override
	public void execute() {
		receiver.getCommand(args[0]);
	}
}
