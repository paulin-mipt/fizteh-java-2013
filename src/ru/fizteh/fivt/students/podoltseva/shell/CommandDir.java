package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.File;

public class CommandDir implements Command {

	@Override
	public String getName() {
		return "dir";
	}

	@Override
	public int getArgsCount() {
		return 0;
	}

	@Override
	public void execute(State state, String[] args) {
		File currentDir = new File(state.getState().toString());
		File[] filesInCurrentDir = currentDir.listFiles();
		for (File i : filesInCurrentDir) {
			System.out.println(i.getName());
		}
	}

}
