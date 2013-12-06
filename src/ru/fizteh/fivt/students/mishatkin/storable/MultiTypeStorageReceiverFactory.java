package ru.fizteh.fivt.students.mishatkin.storable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * Created by Vladimir Mishatkin on 11/12/13
 */
public class MultiTypeStorageReceiverFactory implements TableProviderFactory {
	@Override
	public TableProvider create(String path) throws IOException {
		return new MultiTypeStorageReceiver(path);
	}
}
