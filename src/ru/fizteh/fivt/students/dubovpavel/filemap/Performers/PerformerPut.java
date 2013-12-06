package ru.fizteh.fivt.students.dubovpavel.filemap.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseAccessible;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;

public class PerformerPut<D extends Dispatcher & DataBaseAccessible<String, String>> extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("put") && command.argumentsCount() == 2;
    }

    public void execute(D dispatcher, Command command) throws PerformerException {
        try {
            String old = dispatcher.getDataBase().put(command.getArgument(0), command.getArgument(1));
            if (old == null) {
                dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "new");
            } else {
                dispatcher.callbackWriter(Dispatcher.MessageType.WARNING, String.format("overwrite%n%s", old));
            }
        } catch (DataBaseHandler.DataBaseException e) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("DataBaseException: %s", e.getMessage()));
            throw new PerformerException(e.getMessage());
        }
    }
}
