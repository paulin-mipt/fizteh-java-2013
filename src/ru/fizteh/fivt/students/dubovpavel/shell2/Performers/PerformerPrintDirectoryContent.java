package ru.fizteh.fivt.students.dubovpavel.shell2.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

import java.io.File;

public class PerformerPrintDirectoryContent extends PerformerShell {
    public boolean pertains(Command command) {
        return command.getHeader().equals("dir") && command.argumentsCount() == 0;
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        File directory = getCanonicalFile(".");
        for (String entry : directory.list()) {
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, entry);
        }
    }
}
