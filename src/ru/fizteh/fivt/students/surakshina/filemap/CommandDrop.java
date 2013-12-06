package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandDrop extends DataBaseCommand {
    public CommandDrop(TableState state) {
        super(state);
        name = "drop";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        String name = input[1];
        try {
            state.getTableProvider().removeTable(name);
            System.out.println("dropped");
        } catch (IllegalArgumentException | IllegalStateException e) {
            state.printError(e.getMessage());
            return;
        }
    }

}
