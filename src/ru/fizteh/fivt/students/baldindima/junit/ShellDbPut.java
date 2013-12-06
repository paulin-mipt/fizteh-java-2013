package ru.fizteh.fivt.students.baldindima.junit;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbPut extends ShellIsItCommand {
	private Context context;
	public ShellDbPut(Context nContext){
		setName("put");
		setNumberOfArgs(3);
		context = nContext;
	}
	
	public void run(){
		if (context.table == null){
			System.out.println("no table");
			return;
		}
		String newString = context.table.put(arguments[1], arguments[2]);
		if (newString == null){
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(newString);
		}
	}

}
