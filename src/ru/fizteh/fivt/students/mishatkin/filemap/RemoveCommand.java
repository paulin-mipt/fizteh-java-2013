package ru.fizteh.fivt.students.mishatkin.filemap;
import ru.fizteh.fivt.students.mishatkin.shell.*;
/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class RemoveCommand<Receiver extends FileMapReceiver> extends ShellCommand<Receiver> {
	public RemoveCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(1);
	}

	@Override
	public void execute() {
		receiver.removeCommand(args[0]);
	}
}
