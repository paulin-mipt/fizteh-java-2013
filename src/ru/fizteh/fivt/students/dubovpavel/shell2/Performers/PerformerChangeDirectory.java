package ru.fizteh.fivt.students.dubovpavel.shell2.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

import java.io.File;

public class PerformerChangeDirectory extends PerformerShell {
    public boolean pertains(Command command) {
        return command.getHeader().equals("cd") && command.argumentsCount() == 1;
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        File directory = getCanonicalFile(command.getArgument(0));
        if (directory.isDirectory()) {
            System.setProperty("user.dir", directory.getPath());
        } else {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("%s. cd: '%s' is not a directory", command.getDescription(), directory.getPath())));
        }
    }
}
