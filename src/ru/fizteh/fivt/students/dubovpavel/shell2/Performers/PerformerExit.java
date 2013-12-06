package ru.fizteh.fivt.students.dubovpavel.shell2.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

public class PerformerExit extends PerformerShell {
    public boolean pertains(Command command) {
        return command.getHeader().equals("exit");
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        dispatcher.shutDown();
    }
}
