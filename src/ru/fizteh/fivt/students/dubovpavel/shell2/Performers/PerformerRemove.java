package ru.fizteh.fivt.students.dubovpavel.shell2.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

import java.io.File;

public class PerformerRemove extends PerformerShell {
    public static class PerformerRemoveException extends Exception {
        public PerformerRemoveException(String file) {
            super(file);
        }
    }

    public boolean pertains(Command command) {
        return command.getHeader().equals("rm") && command.argumentsCount() == 1;
    }

    public void removeObject(File object) throws PerformerRemoveException {
        if (object.isDirectory()) {
            for (File subObject : object.listFiles()) {
                if (subObject.isDirectory()) {
                    removeObject(subObject);
                } else if (subObject.isFile()) {
                    if (!subObject.delete()) {
                        throw new PerformerRemoveException(subObject.getPath());
                    }
                }
            }
        }
        if (!object.delete()) {
            throw new PerformerRemoveException(object.getPath());
        }
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        File object = getCanonicalFile(command.getArgument(0));
        if (!object.exists()) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("%s. rm: '%s' does not exist", command.getDescription(), object.getPath())));
        }
        try {
            removeObject(object);
        } catch (PerformerRemoveException e) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("%s. rm: Can not remove '%s'", command.getDescription(), e.getMessage())));
        }
    }
}
