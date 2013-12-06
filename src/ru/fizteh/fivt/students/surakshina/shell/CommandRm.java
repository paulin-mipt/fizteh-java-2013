package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;

public class CommandRm extends AbstractCommand {
    CommandRm(State stateNew) {
        super(stateNew);
        name = "rm";
        numberOfArguments = 1;
    }

    private void deleteFiles(File file) {
        if (file.isDirectory() && (file.listFiles().length != 0)) {
            while (file.listFiles().length != 0) {
                deleteFiles(file.listFiles()[0]);
            }
        }
        file.delete();
    }

    @Override
    public void executeProcess(String[] input) {
        File currentFile = new File(state.getCurrentDirectory(), input[1]);
        if (!currentFile.exists()) {
            state.printError("rm: cannot remove '" + input[1] + "': No such file or directory");
        } else {
            deleteFiles(currentFile);
            currentFile.delete();
        }
    }

}
