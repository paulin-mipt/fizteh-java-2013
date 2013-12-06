package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

/**
 * Created by Vladimir Mishatkin on 11/4/13
 */
public class MultiFileHashMapExtremelyUsefulTableProviderFactory implements TableProviderFactory {
	@Override
	public TableProvider create(String dir) {
		return new MultiFileHashMapReceiver(dir);
	}
}
