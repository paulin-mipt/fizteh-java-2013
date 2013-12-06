package ru.fizteh.fivt.students.dubovpavel.strings.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageAccessible;
import ru.fizteh.fivt.students.dubovpavel.strings.MindfulDataBaseMultiFileHashMap;

public class PerformerSize<D extends Dispatcher & StorageAccessible<Storage<MindfulDataBaseMultiFileHashMap>>>
        extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("size") && command.argumentsCount() == 0;
    }

    public void execute(D dispatcher, Command command) {
        dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS,
                String.valueOf(dispatcher.getStorage().getCurrent().size()));
    }
}
