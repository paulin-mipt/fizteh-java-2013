package ru.fizteh.fivt.students.podoltseva.shell;

public class CommandExit implements Command {

	@Override
	public String getName() {
		return "exit";
	}

	@Override
	public int getArgsCount() {
		return 0;
	}

	@Override
	public void execute(State state, String[] args) {
		System.exit(0);
	}

}
