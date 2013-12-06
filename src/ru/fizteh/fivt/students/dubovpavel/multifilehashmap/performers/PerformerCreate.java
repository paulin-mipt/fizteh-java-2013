package ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageAccessible;

public class PerformerCreate<D extends Dispatcher & StorageAccessible> extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("create") && command.argumentsCount() == 1;
    }

    public void execute(D dispatcher, Command command) throws PerformerException {
        dispatcher.getStorage().create(command.getArgument(0));
    }
}
