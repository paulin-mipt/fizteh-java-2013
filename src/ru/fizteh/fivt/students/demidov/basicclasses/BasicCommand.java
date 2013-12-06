package ru.fizteh.fivt.students.demidov.basicclasses;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.shell.Shell;
import ru.fizteh.fivt.students.demidov.shell.ShellInterruptionException;

public interface BasicCommand {
	abstract void executeCommand(String[] arguments, Shell usedShell) throws IOException, ShellInterruptionException;
	abstract public int getNumberOfArguments();
	abstract public String getCommandName();
}
