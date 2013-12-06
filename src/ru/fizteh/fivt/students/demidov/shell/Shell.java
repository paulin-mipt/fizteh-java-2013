package ru.fizteh.fivt.students.demidov.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.Thread;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Arrays;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicCommand;

public class Shell {
	public ShellMethods curShell;
	
	public Shell(String newCurrentDirectory, InputStream newInStream, PrintStream newOutStream) {
		curShell = new ShellMethods(newCurrentDirectory, newInStream, newOutStream);
	}
	
	public static class ShellMethods {		
		private String currentDirectory;
		private final InputStream inStream;
		private final PrintStream outStream;
		private final Map<String, BasicCommand> loadedCommands;
		
		public ShellMethods(String newCurrentDirectory, InputStream newInStream, PrintStream newOutStream) {
				currentDirectory = newCurrentDirectory;
				inStream = newInStream;
				outStream = newOutStream;
				loadedCommands = new HashMap<String, BasicCommand>();
		}
		
		public void loadCommand(BasicCommand currentCommand) {
			loadedCommands.put(currentCommand.getCommandName(), currentCommand);
		}
		
		public String getCurrentDirectory() {
				return currentDirectory;	
		}
		
		public InputStream getInStream() {
			return inStream;	
		}
		
		public PrintStream getOutStream() {
			return outStream;	
		}
		
		public void changeCurrentDirectory(String newDirectory) {
			currentDirectory = newDirectory;		
		}		
		
		public BasicCommand getCommandToExecute(String commandName) {
			return loadedCommands.get(commandName);
		}
	}
	
	public void startShell(String[] arguments) {		
		if (0 != arguments.length) {
			StringBuilder strBuilderOfInstructions = new StringBuilder();
			for (int i = 0; i < arguments.length; ++i) {
				strBuilderOfInstructions.append(arguments[i] + " ");
			}			
			String instructions = strBuilderOfInstructions.toString();

			processBatchMode(instructions, curShell);
		} else {
			processInteractiveMode(curShell);
		}
	}
	
	private void processBatchMode(String instructions, ShellMethods curShell) {
		try {
			doInstructions(instructions, curShell);
		} catch (IOException catchedException) {
			System.err.println(catchedException.getMessage());
			System.exit(1);
		} catch (ShellInterruptionException catchedException) {
			return;
		}
	}
	
	private void processInteractiveMode(ShellMethods curShell) {
		Scanner inputScanner = new Scanner(System.in);

		while (!Thread.currentThread().isInterrupted()) {
			System.out.print(curShell.getCurrentDirectory() + "$ ");

			String instructions = inputScanner.nextLine();				
	
			try {
				doInstructions(instructions, curShell);
			} catch (IOException catchedException) {
				System.err.println(catchedException.getMessage());
			} catch (ShellInterruptionException catchedException) {
				return;
			}
		}
	}

	private void doInstructions(String instructions, ShellMethods curShell) throws IOException, ShellInterruptionException {
		for (String instruction : instructions.trim().split("\\s*;\\s*", -1)) {
			executeInstruction(instruction.split("\\s+(?![^\\(]*\\))"), curShell);
		}
	}

	private void executeInstruction(String[] exeInstruction, ShellMethods curShell) throws IOException, ShellInterruptionException {
		if ((exeInstruction.length == 0) || (exeInstruction[0].equals(""))) {
			return;
		}
		
		BasicCommand exeCommand = curShell.getCommandToExecute(exeInstruction[0]);
		
		if (null == exeCommand) {
			throw new IOException("unknown instruction: " + exeInstruction[0]);
		} 
		
		if (exeInstruction.length > exeCommand.getNumberOfArguments() + 1) {
			throw new IOException("too many arguments for " + exeInstruction[0]);
		} else if (exeInstruction.length < exeCommand.getNumberOfArguments() + 1) {
			throw new IOException("too less arguments for " + exeInstruction[0]);
		}

		exeCommand.executeCommand(Arrays.copyOfRange(exeInstruction, 1, exeInstruction.length), this);
	}
}
