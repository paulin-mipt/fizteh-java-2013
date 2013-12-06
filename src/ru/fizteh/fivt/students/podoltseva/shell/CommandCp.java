package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandCp implements Command {

	@Override
	public String getName() {
		return "cp";
	}

	@Override
	public int getArgsCount() {
		return 2;
	}

	@Override
	public void execute(State state, String[] args)
			throws FileNotFoundException, IOException {
		Path sourceAbsolutePath = Paths.get(args[0]);
		if (!sourceAbsolutePath.isAbsolute()) {
			sourceAbsolutePath = state.getState().resolve(sourceAbsolutePath);
		}
		File source = new File(sourceAbsolutePath.toString());
		if (!source.exists()) {
			throw new FileNotFoundException("cp: '" + args[0] + "': No such file or directory");
		}
		Path destinationAbsolutePath = Paths.get(args[1]);
		if (!destinationAbsolutePath.isAbsolute()) {
			destinationAbsolutePath = state.getState().resolve(destinationAbsolutePath);
		}
		File destination = new File(destinationAbsolutePath.toString());
		if (!destination.exists()) {
			if (source.isFile()) {
				Files.copy(sourceAbsolutePath, destinationAbsolutePath);
			} else {
				recursiveCopyFile(source, destinationAbsolutePath);
			}
			return;
		}
		if (sourceAbsolutePath.toString().equals(destinationAbsolutePath.toString())) {
			if (source.isFile()) {
				throw new IOException("cp: '" + args[0] + "' and '" + args[1] + "' - are the same file");
			} else { 
				throw new IOException("cp: It is impossible to copy the directory '" + args[0] + "' into itself");
			}
		}
		if (source.isFile() && destination.isFile()) {
			throw new IOException("cp: The file with name '" + args[1] + "' already exists.");
		} else if (source.isFile() && destination.isDirectory()) {
			Files.copy(sourceAbsolutePath, destinationAbsolutePath.resolve(source.getName()));
		} else if (source.isDirectory() && destination.isFile()) {
			throw new IOException("cp: Can't overwrite file '" + args[1] + "' with directory '" + args[0] + "'.");
		} else if (source.isDirectory() && destination.isDirectory()) {
			recursiveCopyFile(source, destinationAbsolutePath.resolve(source.getName()));
		}
	}
	
	private void recursiveCopyFile(File source, Path destination) throws IOException {
		Files.copy(source.toPath(), destination);
		if (source.isDirectory()) {
			File[] sourceFileList = source.listFiles();
			for (File i : sourceFileList) {
				recursiveCopyFile(i, destination.resolve(i.getName()));
			}
		}
	}

}
