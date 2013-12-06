package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;

public class CommandDir extends AbstractCommand {
    public CommandDir(State stateNew) {
        super(stateNew);
        name = "dir";
        numberOfArguments = 0;
    }

    @Override
    public void executeProcess(String[] input) {
        for (File file : state.getCurrentDirectory().listFiles()) {
            System.out.println(file.getName());
        }
    }
}
