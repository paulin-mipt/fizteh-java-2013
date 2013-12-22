package ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseAccessible;

public class PerformerSafeDataBaseOperationWrapper<D extends Dispatcher & DataBaseAccessible> extends Performer<D> {
    private Performer performer;

    public PerformerSafeDataBaseOperationWrapper(Performer p) {
        performer = p;
    }

    public boolean pertains(Command command) {
        return performer.pertains(command);
    }

    public void execute(D dispatcher, Command command) throws PerformerException {
        if (dispatcher.getDataBase() == null) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, "no table");
        } else {
            performer.execute(dispatcher, command);
        }
    }
}
