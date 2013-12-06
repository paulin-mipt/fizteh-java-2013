package ru.fizteh.fivt.students.demidov.shell;

public class Main {
	public static void main(String[] arguments) {
		Shell usedShell = new Shell(System.getProperty("user.dir"), System.in, System.out);
		usedShell.curShell.loadCommand(new Cd());
		usedShell.curShell.loadCommand(new Cp());
		usedShell.curShell.loadCommand(new Mkdir());
		usedShell.curShell.loadCommand(new Dir());
		usedShell.curShell.loadCommand(new Mv());
		usedShell.curShell.loadCommand(new Pwd());
		usedShell.curShell.loadCommand(new Rm());
		usedShell.curShell.loadCommand(new Exit());
		
		usedShell.startShell(arguments);
	}
}
