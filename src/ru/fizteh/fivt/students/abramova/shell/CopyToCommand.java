package ru.fizteh.fivt.students.abramova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.*;

public class CopyToCommand extends Command {

    public CopyToCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        Stage stage = status.getStage();
        if (stage == null) {
            throw new IllegalStateException(getName() + ": Command do not get stage");
        }
        File source = new File(stage.currentDirPath(), args[0]);
        if (!source.exists()) {
            System.out.println(getName() + ": cannot copy \'" + args[0] + "\': No such file or directory");
            return 1;
        } else {
            File destination = new File(stage.currentDirPath(), args[1]);
            if (destination.exists()) {
                if (isSubfolder(source, destination)) {
                    System.out.println("Destination " + args[1] + " is subfolder of " + args[0]);
                    return 2;
                }
                String[] argsToRemove = new String[1];
                argsToRemove[0] = args[1];
                new RemoveCommand("rm").doCommand(argsToRemove, status);
            }
            if (source.canRead()) {
                try {
                    Files.copy(source.toPath(), destination.toPath(), REPLACE_EXISTING , COPY_ATTRIBUTES);
                } catch (IOException e) {
                    throw new IOException(getName() + ": Copy error: " + e.getMessage());
                }
                if (source.isDirectory()) {
                    //Копируем внутренноть папки
                    for (String sourceChild : source.list()) {
                        String[] newArgs = new String[2];
                        newArgs[0] = new File(args[0], sourceChild).getPath();
                        newArgs[1] = new File(args[1], sourceChild).getPath();
                        doCommand(newArgs, status);
                    }
                }
            } else {
                throw new IOException(getName() + ": file " + args[0] + " cannot be read");
            }
        }
        return 0;
    }

    private boolean isSubfolder(File source, File destination) {
        return !destination.toPath().equals(destination.toPath().getRoot()) || !source.equals(destination)
                ||  isSubfolder(source, destination.getParentFile());
    }

    @Override
    public boolean correctArgs(String[] args) {
        boolean returnValue = args != null;
        if (returnValue) {
            returnValue = args.length == 2;
        }
        return returnValue;
    }
}
