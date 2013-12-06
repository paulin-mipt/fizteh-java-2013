package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.FileNotFoundException;

public class CommandPwd implements Command {

	@Override
	public String getName() {
		return "pwd";
	}

	@Override
	public int getArgsCount() {
		return 0;
	}

	@Override
	public void execute(State state, String[] args)
			throws FileNotFoundException {
		System.out.println(state.getState().toString());
	}

}
