package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CommandMv extends AbstractCommand {
    public CommandMv(State stateNew) {
        super(stateNew);
        name = "mv";
        numberOfArguments = 2;
    }

    private File unionWithCurrentPath(String curr) {
        File curr1 = new File(curr);
        if (!curr1.isAbsolute()) {
            curr1 = new File(state.getCurrentDirectory(), curr);
            try {
                curr1 = curr1.getCanonicalFile();
            } catch (IOException exception) {
                state.printError(curr1.toString());
            }
        }
        return curr1;
    }

    @Override
    public void executeProcess(String[] input) {
        String source = input[1];
        String destination = input[2];
        if (input[2].contains(" ")) {
            state.printError("Incorrect number of arguments");
        } else {
            File currentFile = unionWithCurrentPath(source);
            File destinationFile = new File(destination, source);
            if (!destinationFile.isAbsolute()) {
                destinationFile = new File(state.getCurrentDirectory() + File.separator + destination + File.separator
                        + source);
            }
            File file = unionWithCurrentPath(destination);
            if (!currentFile.exists()) {
                state.printError("mv: cannot move: '" + source + "': No such file or directory");
            } else if (!file.exists()) {
                destinationFile = file;
            }
            try {
                Files.move(currentFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException exception) {
                state.printError("cp: cannot move '" + source + "'");
            }
        }
    }
}
