package ru.fizteh.fivt.students.dubovpavel.filemap.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseAccessible;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;

public class PerformerGet<D extends Dispatcher & DataBaseAccessible<String, String>> extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("get") && command.argumentsCount() == 1;
    }

    public void execute(D dispatcher, Command command) throws PerformerException {
        try {
            String value = dispatcher.getDataBase().get(command.getArgument(0));
            if (value == null) {
                dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, "not found");
            } else {
                dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, String.format("found%n%s", value));
            }
        } catch (DataBaseHandler.DataBaseException e) {
            throw new PerformerException(e.getMessage());
        }
    }
}
