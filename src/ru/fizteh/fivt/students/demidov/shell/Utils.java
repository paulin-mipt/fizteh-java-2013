package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
	public static File getFile(String fileName, Shell usedShell) throws IOException {
		File resultFile = new File(fileName);

		if (resultFile.isAbsolute()) {
			return resultFile.getCanonicalFile();
		} else {
			return new File(usedShell.curShell.getCurrentDirectory(), fileName).getCanonicalFile();
		}
	}
	
	public static void copy(File source, File destination) throws IOException {
		if (destination.exists()) {
			throw new IOException("file " + destination.getPath() + " already exists");
		}
		
		FileChannel sourceChannel = new FileInputStream(source.getPath()).getChannel();
		FileChannel destinationChannel = new FileOutputStream(destination.getPath()).getChannel();

		destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	
		sourceChannel.close();
		destinationChannel.close();
	} 
	
	public static void copyFileOrDirectory(File source, File destination) throws IOException {
		if ((destination.isFile()) && (!(source.isFile()))) {
			throw new IOException("unable to copy directory " + source.getPath() + " to file " + destination.getPath());
		}
		
		if ((source.isFile()) && (destination.isDirectory())) {
			copy(source, new File(destination.getPath(), source.getName()));
		} else if (source.isFile()) {
			copy(source, destination);
		} else {
			File elementsDestination = new File(destination.getPath(), source.getName());
			
			if(!elementsDestination.exists()) {
				elementsDestination.mkdir();
			}

			for (String element : source.list()) {
				copyFileOrDirectory(new File(source.getPath(), element), elementsDestination);
			}
		}
	}
	
	public static void deleteFileOrDirectory(File source) {
		if (source.isFile()) {
			source.delete();
			return;
		}
		
		for (String element : source.list()) {
			deleteFileOrDirectory(new File(source.getPath(), element));
		}

		source.delete();
	}
	
	private Utils() {}
}
