package ru.fizteh.fivt.students.baldindima.junit;



import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbRemove extends ShellIsItCommand {
	private Context context;
	public ShellDbRemove(Context nContext){
		setName("remove");
		setNumberOfArgs(2);
		context = nContext;
	}
	
	
	public void run() {
		if (context.table == null){
			System.out.println("no table");
			return;
		}
		Storeable storeable = context.table.remove(arguments[1]);
		if (storeable == null){
			System.out.println("not found");
		} else {
			System.out.println("removed");
			
		}
	}

}

