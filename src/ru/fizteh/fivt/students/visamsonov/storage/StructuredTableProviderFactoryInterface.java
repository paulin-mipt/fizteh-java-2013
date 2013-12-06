package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import java.io.IOException;

public interface StructuredTableProviderFactoryInterface extends TableProviderFactory {

	StructuredTableProviderInterface create (String dir) throws IOException;
}