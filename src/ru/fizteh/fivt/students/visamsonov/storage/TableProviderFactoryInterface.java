package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public interface TableProviderFactoryInterface extends TableProviderFactory {

	TableProviderInterface create (String dir);
}