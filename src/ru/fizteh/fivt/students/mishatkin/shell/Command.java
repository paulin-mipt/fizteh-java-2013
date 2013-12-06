package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * Command.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

public interface Command<Receiver extends CommandReceiver> {
	public String getName();
	public int getArgumentsCount();
	public void setArguments(String[] arguments);
	public void setReceiver(Receiver receiver);
	public void execute() throws ShellException;
}

