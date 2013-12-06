package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandMv implements Command {

	@Override
	public String getName() {
		return "mv";
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
			throw new FileNotFoundException("mv: '" + args[0] + "': No such file or directory");
		}
		Path destinationAbsolutePath = Paths.get(args[1]);
		if (!destinationAbsolutePath.isAbsolute()) {
			destinationAbsolutePath = state.getState().resolve(destinationAbsolutePath);
		}
		File destination = new File(destinationAbsolutePath.toString());
		if (!destination.exists()) {
			if(!destinationAbsolutePath.getParent().equals(state.getState())) {
				throw new FileNotFoundException("mv: '" + "': No such file or directory.");
			} else {
				source.renameTo(destination);
				return;
			}
		} 
		if (sourceAbsolutePath.toString().equals(destinationAbsolutePath.toString())) {
			if (source.isFile()) {
				throw new IOException("mv: '" + args[0] + "' and '" + args[1] + "' - are the same file.");
			} else { 
				throw new IOException("mv: It is impossible to move the directory '" + args[0] + "' into itself.");
			}
		}
		if (source.isFile() && destination.isFile()) {
			throw new IOException("mv: The file with name '" + args[1] + "' already exists.");
		} else if (source.isFile() && destination.isDirectory()) {
			Files.move(sourceAbsolutePath, destinationAbsolutePath.resolve(source.getName()));
		} else if (source.isDirectory() && destination.isFile()) {
			throw new IOException("mv: Can't replace file '" + args[1] + "' with directory '" + args[0] + "'.");
		} else if (source.isDirectory() && destination.isDirectory()) {
			recursiveMoveFile(source, destination);
		}
	}
	
	private void recursiveMoveFile(File source, File destination) throws IOException {
		Files.move(source.toPath(), destination.toPath().resolve(source.getName()));
		if (source.isDirectory()) {
			File[] sourceFileList = source.listFiles();
			destination = new File(destination.toPath().resolve(source.getName()).toString());
			for (File i : sourceFileList) {
				recursiveMoveFile(i, destination);
			}
		}
	}

}
