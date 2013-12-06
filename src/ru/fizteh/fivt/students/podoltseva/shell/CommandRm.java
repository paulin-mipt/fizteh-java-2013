package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandRm implements Command {

	@Override
	public String getName() {
		return "rm";
	}

	@Override
	public int getArgsCount() {
		return 1;
	}

	@Override
	public void execute(State state, String[] args)
			throws FileNotFoundException, IOException {
		Path pathFileForDelete = Paths.get(args[0]);
		if (!pathFileForDelete.isAbsolute()) {
			pathFileForDelete = state.getState().resolve(pathFileForDelete);
		}
		File fileForDelete = new File(pathFileForDelete.toString());
		if (!fileForDelete.exists()) {
			throw new FileNotFoundException("rm: '" + args[0] + "': No such file or directory");
		}
		recursiveDelete(pathFileForDelete);
	}
	
	private void recursiveDelete(Path pathFileForDelete) throws IOException {
		File fileForDelete = new File(pathFileForDelete.toString());
		if (fileForDelete.isDirectory()) {
			File[] filesInDir = fileForDelete.listFiles();
			for (File i : filesInDir) {
				recursiveDelete(pathFileForDelete.resolve(i.getName()));
			}
		}
		Files.delete(pathFileForDelete);
	}

}
