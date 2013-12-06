package ru.fizteh.fivt.students.surakshina.shell;

public class CommandExit extends AbstractCommand {
    public CommandExit(State stateNew) {
        super(stateNew);
        name = "exit";
        numberOfArguments = 0;
    }

    @Override
    public void executeProcess(String[] input) {
        System.exit(0);
    }
}
