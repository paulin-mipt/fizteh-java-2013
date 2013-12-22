package ru.fizteh.fivt.students.dubovpavel.shell2.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

import java.io.File;

public class PerformerMove extends PerformerShell {
    private static class PerformerMoveException extends Exception {
        public PerformerMoveException(String msg) {
            super(msg);
        }
    }

    public boolean pertains(Command command) {
        return command.getHeader().equals("mv") && command.argumentsCount() == 2;
    }

    private void moveRecursively(File source, File destination) throws PerformerMoveException {
        if (source.isDirectory()) {
            for (File object : source.listFiles()) {
                if (object.isFile()) {
                    if (!object.renameTo(new File(destination, object.getName()))) {
                        throw new PerformerMoveException(object.getPath());
                    }
                } else if (object.isDirectory()) {
                    moveRecursively(object, new File(destination, object.getName()));
                }
            }
        }
        if (!source.renameTo(destination)) {
            throw new PerformerMoveException(source.getPath());
        }
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        File object = getCanonicalFile(command.getArgument(0));
        if (!object.exists()) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("%s. cp: '%s' does not exist", command.getDescription(), object.getPath())));
        }
        try {
            moveRecursively(object, getCanonicalFile(command.getArgument(1)));
        } catch (PerformerMoveException e) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("%s. mv: Can not move '%s'", command.getDescription(), e.getMessage())));
        }
    }
}
