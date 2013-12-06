package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Shell {
	private State state = new State();
	
	public Shell() {
		state.setState(Paths.get(new File(".").getAbsolutePath()).normalize());
	}
	
	public void batchMode(String[] args) {
		String inputString = join(args, " ");
		inputString.trim();
		String[] commands = inputString.split("\\s*;\\s*");
		CommandExecutor exec = new CommandExecutor();
		try {
			for (String i : commands) {
				exec.execute(state, i);
			}
		}
		catch (FileNotFoundException exception) {
			System.err.println(exception.getMessage());
			System.exit(2);
		}
		catch (IOException exception) {
			System.err.println(exception.getMessage());
			System.exit(1);
		}
	}
	
	public void interactiveMode() {
		CommandExecutor exec = new CommandExecutor();
		String inputString;
		String[] commands;
		Scanner scan = new Scanner(System.in);
		do {
			try {
				System.out.println(state.getState() + "$ ");
				inputString = scan.nextLine();
				inputString = inputString.trim();
				commands = inputString.split("\\s*;\\s*");
				for (String i : commands) {
					exec.execute(state, i);
				}
			}
			catch (FileNotFoundException exception) {
				System.err.println(exception.getMessage());
			}
			catch (IOException exception) {
				System.err.println(exception.getMessage());
			}
		}
		while(!Thread.currentThread().isInterrupted());
		
	}
	
	private static String join(/*Iterable<?>*/ String[] objects, String Separator) {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Object o : objects) {
			if (first) {
				result.append(o.toString());
				first = false;
			} else {
				result.append(Separator).append(o.toString());
			}
		}
		return result.toString();
	}
}
