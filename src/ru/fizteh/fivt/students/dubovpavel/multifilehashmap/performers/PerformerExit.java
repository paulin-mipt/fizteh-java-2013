package ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageAccessible;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageException;

public class PerformerExit<D extends Dispatcher & StorageAccessible> extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("exit") && command.argumentsCount() == 0;
    }

    public void execute(D dispatcher, Command command) throws PerformerException {
        try {
            dispatcher.getStorage().save();
            dispatcher.shutDown();
        } catch (StorageException e) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("Can not exit, use 'halt' if you really want to: %s", e.getMessage())));
        }
    }
}
