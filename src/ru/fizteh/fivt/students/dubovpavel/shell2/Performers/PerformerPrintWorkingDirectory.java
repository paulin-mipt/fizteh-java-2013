package ru.fizteh.fivt.students.dubovpavel.shell2.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

public class PerformerPrintWorkingDirectory extends PerformerShell {
    public boolean pertains(Command command) {
        return command.getHeader().equals("pwd") && command.argumentsCount() == 0;
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, getCanonicalFile(".").getPath());
    }
}
