package ru.fizteh.fivt.students.podoltseva.shell;

public class Main {

	public static void main(String[] args) {
		Shell shell = new Shell();
		if (args.length == 0) {
			shell.interactiveMode();
		} else {
			shell.batchMode(args);
		}
		System.exit(0);
	}

}
