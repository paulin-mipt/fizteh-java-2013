package ru.fizteh.fivt.students.demidov.junit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TableProviderFactoryImplementation implements TableProviderFactory {
	public TableProviderFactoryImplementation() {}
	
	public TableProviderImplementation create(String dir) {
		if (dir == null) {
			throw new IllegalArgumentException("null basic directory");
		}		
		return new TableProviderImplementation(dir);
	}
}
