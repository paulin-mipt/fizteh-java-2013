package ru.fizteh.fivt.students.dubovpavel.strings.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageAccessible;
import ru.fizteh.fivt.students.dubovpavel.strings.MindfulDataBaseMultiFileHashMap;

public class PerformerUse<D extends Dispatcher & StorageAccessible<Storage<MindfulDataBaseMultiFileHashMap>>>
        extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("use") && command.argumentsCount() == 1;
    }

    public void execute(D dispatcher, Command command) {
        if (dispatcher.getStorage().getCurrent() != null) {
            int diff = dispatcher.getStorage().getCurrent().getDiff();
            if (diff != 0) {
                dispatcher.callbackWriter(Dispatcher.MessageType.WARNING, String.format("%d unsaved changes", diff));
                return;
            }
        }
        dispatcher.getStorage().setCurrent(command.getArgument(0));
    }
}
