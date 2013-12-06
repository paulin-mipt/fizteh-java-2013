package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;

import java.lang.reflect.Method;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class PutCommand<Receiver extends FileMapReceiver>  extends ShellCommand<Receiver> {
	public PutCommand(FileMapReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(2);
	}

	@Override
	public void execute() {
		receiver.putCommand(args[0], args[1]);
	}
}
