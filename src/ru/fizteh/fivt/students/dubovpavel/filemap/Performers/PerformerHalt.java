package ru.fizteh.fivt.students.dubovpavel.filemap.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

public class PerformerHalt<D extends Dispatcher> extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("halt") && command.argumentsCount() == 0;
    }

    public void execute(D dispatcher, Command command) throws PerformerException {
        dispatcher.shutDown();
    }
}
