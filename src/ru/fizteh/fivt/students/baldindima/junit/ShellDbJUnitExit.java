package ru.fizteh.fivt.students.baldindima.junit;

import ru.fizteh.fivt.students.baldindima.shell.ExitException;
import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

	public class ShellDbJUnitExit extends ShellIsItCommand {
		private Context context;
		public ShellDbJUnitExit(Context nContext){
			context = nContext;
			setName("exit");
			setNumberOfArgs(1);
		}
		public void run(){
			
			context.table.commit();
			if (context.getChanges() == 0) {
	            throw new ExitException();
	        } else {
	            System.out.println(context.getChanges() + " unsaved changes");
	        }

	}

	}

