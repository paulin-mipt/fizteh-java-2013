package ru.fizteh.fivt.students.podoltseva.shell;

import java.nio.file.Path;

public class State {
	private Path currentPath;
	
	public Path getState() {
		return currentPath;
	}
	
	public void setState(Path newPath) {
		currentPath = newPath;
	}
}
