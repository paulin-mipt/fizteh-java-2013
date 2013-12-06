package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellException;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;

import java.io.File;

/**
 * Created by Vladimir Mishatkin on 11/4/13
 */
public class SandboxTest {
	protected ShellReceiver shell;
	private static final String SANDBOX_DIRECTORY_NAME = "JUnitDBSandbox";
	protected File sandbox;

	public void prepare() throws Exception {
		shell = new ShellReceiver();
		shell.makeDirectoryCommand(SANDBOX_DIRECTORY_NAME);
		sandbox = new File(SANDBOX_DIRECTORY_NAME);
	}

	public void reversePrepare() throws Exception {
		try {
			shell.rmCommand(SANDBOX_DIRECTORY_NAME);
		} catch (ShellException cucumber) {
			cucumber.printStackTrace();
		}
	}
}
