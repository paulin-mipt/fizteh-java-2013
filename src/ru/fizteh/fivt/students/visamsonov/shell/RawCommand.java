package ru.fizteh.fivt.students.visamsonov.shell;

public final class RawCommand {
	
	public final String name;
	public final String args;

	public RawCommand (String commandName, String commandArgs) {
		this.name = commandName;
		this.args = commandArgs;
	}
}