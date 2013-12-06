package ru.fizteh.fivt.students.dubovpavel.shell2.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PerformerCopy extends PerformerShell {
    private static class PerformerCopyException extends Exception {
        public PerformerCopyException(String msg) {
            super(msg);
        }
    }

    public boolean pertains(Command command) {
        return command.getHeader().equals("cp") && command.argumentsCount() == 2;
    }

    private void copyFile(String source, String destination) throws PerformerCopyException {
        if (source.equals(destination)) {
            throw new PerformerCopyException(String.format("Attempt to copy '%s' in itself", source));
        }
        try {
            FileInputStream inf = new FileInputStream(source);
            FileOutputStream ouf = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inf.read(buffer)) > 0) {
                ouf.write(buffer, 0, length);
            }
            inf.close();
            ouf.close();
        } catch (IOException e) {
            throw new PerformerCopyException(String.format("Can not copy '%s' to '%s'", source, destination));
        }
    }

    private void copyFolder(String source, String destination) throws PerformerCopyException {
        File sourceFolder = new File(source);
        File destinationFolder = new File(destination);
        if (!destinationFolder.mkdir()) {
            throw new PerformerCopyException(String.format("Can not create '%s'", destinationFolder));
        }
        for (File object : sourceFolder.listFiles()) {
            if (object.isFile()) {
                copyFile(object.getPath(), new File(destinationFolder, object.getName()).getPath());
            } else if (object.isDirectory()) {
                copyFolder(object.getPath(), new File(destinationFolder, object.getName()).getPath());
            }
        }
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        File object = getCanonicalFile(command.getArgument(0));
        String canonicalSource = object.getPath();
        String canonicalDestination = getCanonicalFile(command.getArgument(1)).getPath();
        if (!object.exists()) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("%s. cp: '%s' does not exist", command.getDescription(), canonicalSource)));
        }
        try {
            if (object.isFile()) {
                copyFile(canonicalSource, canonicalDestination);
            } else if (object.isDirectory()) {
                copyFolder(canonicalSource, canonicalDestination);
            }
        } catch (PerformerCopyException e) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("%s. cp: %s.", command.getDescription(), e.getMessage())));
        }
    }
}
