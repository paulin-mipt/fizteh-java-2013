package ru.fizteh.fivt.students.dubovpavel.filemap.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseAccessible;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;

public class PerformerExit<D extends Dispatcher & DataBaseAccessible<String, String>> extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("exit");
    }

    public void execute(D dispatcher, Command command) throws PerformerException {
        try {
            dispatcher.getDataBase().save();
            dispatcher.shutDown();
        } catch (DataBaseHandler.DataBaseException e) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("Can not exit, use 'halt' if you really want to: %s", e.getMessage())));
        }
    }
}
