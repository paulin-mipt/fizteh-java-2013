package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandMkdir implements Command {

	@Override
	public String getName() {
		return "mkdir";
	}
	
	@Override
	public int getArgsCount() {
		return 1;
	}
	
	@Override
	public void execute(State state, String[] args)
			throws IOException {
		Path newDirPath = state.getState().resolve(args[0]);
		if (newDirPath.toFile().exists()) {
			throw new IOException("mkdir: The directory or file '" + args[0] + "' is already exists.");
		}
		Files.createDirectory(newDirPath);
	}	

}
