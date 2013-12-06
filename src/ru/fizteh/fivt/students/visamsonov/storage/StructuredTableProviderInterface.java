package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.structured.TableProvider;
import java.io.IOException;
import java.util.List;

public interface StructuredTableProviderInterface extends TableProvider {

	StructuredTableInterface getTable (String name);

	StructuredTableInterface createTable (String name, List<Class<?>> columnTypes) throws IOException;
}