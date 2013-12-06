package ru.fizteh.fivt.students.mishatkin.shell;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Vladimir Mishatkin on 10/18/13
 */
public abstract class ShellCommand<Receiver extends ShellReceiver> implements Command<Receiver> {
	private int inputArgumentsCount;
	protected String[] args = new String[2];
	protected Receiver receiver;

	protected ShellCommand(Receiver receiver) {	//	always implement this with super(receiver) in subclasses!
		this.receiver = receiver;
	}

	protected void setInputArgumentsCount(int newInputArgumentsCount) {
		inputArgumentsCount = newInputArgumentsCount;
	}

	@Override
	public int getArgumentsCount() {
		return inputArgumentsCount;
	}

	@Override
	public String getName() {
		String className = getClass().getName().toLowerCase();
		return className.substring(className.lastIndexOf(".") + 1, className.length() - "Command".length()).toLowerCase();
	}

	@Override
	public void setArguments(String[] arguments) {
		this.args = arguments;
	}

	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
}
