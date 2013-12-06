package ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageAccessible;

public class PerformerDrop<D extends Dispatcher & StorageAccessible> extends Performer<D> {
    public boolean pertains(Command command) {
        return command.getHeader().equals("drop") && command.argumentsCount() == 1;
    }

    public void execute(D dispatcher, Command command) {
        dispatcher.getStorage().drop(command.getArgument(0));
    }
}
