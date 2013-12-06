package ru.fizteh.fivt.students.dubovpavel.strings.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageAccessible;
import ru.fizteh.fivt.students.dubovpavel.strings.MindfulDataBaseMultiFileHashMap;

public class PerformerCommit<D extends Dispatcher & StorageAccessible<Storage<MindfulDataBaseMultiFileHashMap>>>
        extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("commit") && command.argumentsCount() == 0;
    }

    public void execute(D dispatcher, Command command) {
        try {
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS,
                    String.valueOf(dispatcher.getStorage().getCurrent().commit()));
        } catch (DataBaseHandler.DataBaseException e) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, e.getMessage());
        }
    }
}
