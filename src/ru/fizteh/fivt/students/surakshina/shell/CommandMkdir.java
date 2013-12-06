package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;

public class CommandMkdir extends AbstractCommand {
    public CommandMkdir(State stateNew) {
        super(stateNew);
        name = "mkdir";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        File currentFile = new File(state.getCurrentDirectory(), input[1]);
        if (currentFile.exists()) {
            state.printError("mkdir: can't create a directory '" + input[1] + "': such directory exists");
        } else if (!currentFile.mkdirs()) {
            state.printError("mkdir can't create a directory'" + input[1]);
        }
    }
}
