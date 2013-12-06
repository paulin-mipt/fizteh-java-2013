package ru.fizteh.fivt.students.mishatkin.storable;

import ru.fizteh.fivt.students.mishatkin.multifilehashmap.CreateCommand;
import ru.fizteh.fivt.students.mishatkin.multifilehashmap.MultiFileHashMapReceiver;

/**
 * Created by Vladimir Mishatkin on 11/11/13
 */
public class CreateMultiTypeTableCommand extends CreateCommand {
	public CreateMultiTypeTableCommand(MultiFileHashMapReceiver receiver) {
		super(receiver);
	}
}
