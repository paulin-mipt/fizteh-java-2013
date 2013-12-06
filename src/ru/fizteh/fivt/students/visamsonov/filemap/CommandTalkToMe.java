package ru.fizteh.fivt.students.visamsonov.filemap;

import java.io.*;
import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

public class CommandTalkToMe extends CommandAbstract<ShellState> {

	public CommandTalkToMe () {
		super("talk-to-me");
	}
	
	public boolean evaluate (ShellState state, String args) {
		getOutStream().println("Are you kidding me?!\nResult: 42");
		return true;
	}
}