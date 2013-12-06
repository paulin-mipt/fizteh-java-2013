package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.strings.TableProvider;

public interface TableProviderInterface extends TableProvider {

	TableInterface getTable (String name);

	TableInterface createTable (String name);
}