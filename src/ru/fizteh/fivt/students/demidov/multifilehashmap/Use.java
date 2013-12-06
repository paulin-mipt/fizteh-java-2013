package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicDataBaseState;
import ru.fizteh.fivt.students.demidov.basicclasses.BasicTable;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Use<ElementType, TableType extends BasicTable<ElementType>> extends BasicMultiFileHashMapCommand<ElementType, TableType> {
	public Use(BasicDataBaseState<ElementType, TableType> dataBaseState) {
		super(dataBaseState, "use", 1);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		dataBaseState.use(arguments[0]);
		usedShell.curShell.getOutStream().println("using " + arguments[0]);
	}
}
