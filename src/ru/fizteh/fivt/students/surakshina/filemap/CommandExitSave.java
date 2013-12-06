package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandExitSave extends DataBaseCommand {
    public CommandExitSave(TableState state) {
        super(state);
        name = "exit";
        numberOfArguments = 0;
    }

    @Override
    public void executeProcess(String[] input) {
        System.exit(0);
    }
}
