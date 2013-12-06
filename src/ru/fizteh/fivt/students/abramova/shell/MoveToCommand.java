package ru.fizteh.fivt.students.abramova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class MoveToCommand extends Command {

    public MoveToCommand(String name) {
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
            System.out.println(getName() + ": cannot move \'" + args[0] + "\': No such file or directory");
            return 1;
        } else {
            File destination = new File(stage.currentDirPath(), args[1]);
            if (destination.exists()) {
                String[] argsToRemove = new String[1];
                argsToRemove[0] = args[1];
                new RemoveCommand("rm").doCommand(argsToRemove, status);
            }
            if (isSubfolder(source, destination)) {
                System.out.println("Destination " + args[1] + " is subfolder of " + args[0]);
                return 2;
            }
            try {
                Files.move(source.toPath(), destination.toPath());
            } catch (IOException e) {
                throw new IOException(getName() + ": Move error: " + e.getMessage());
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
        return args != null && args.length == 2;
    }
}
