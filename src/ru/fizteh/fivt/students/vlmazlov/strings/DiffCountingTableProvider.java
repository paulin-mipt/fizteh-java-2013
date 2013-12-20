package ru.fizteh.fivt.students.vlmazlov.strings;

import ru.fizteh.fivt.storage.strings.TableProvider;

public interface DiffCountingTableProvider extends TableProvider {
	@Override
	DiffCountingTable getTable(String name);

	@Override
	DiffCountingTable createTable(String name);

}