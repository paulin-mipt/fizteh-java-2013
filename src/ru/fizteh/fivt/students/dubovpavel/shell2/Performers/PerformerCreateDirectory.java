package ru.fizteh.fivt.students.dubovpavel.shell2.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

import java.io.File;

public class PerformerCreateDirectory extends PerformerShell {
    public boolean pertains(Command command) {
        return command.getHeader().equals("mkdir") && command.argumentsCount() == 1;
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        File directory = getCanonicalFile(command.getArgument(0));
        if (!directory.mkdirs()) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("'%s'. mkdir: Can not create '%s'", command.getDescription(), directory.getPath())));
        }
    }
}
