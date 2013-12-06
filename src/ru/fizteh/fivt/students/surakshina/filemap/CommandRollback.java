package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandRollback extends DataBaseCommand {
    public CommandRollback(TableState state) {
        super(state);
        name = "rollback";
        numberOfArguments = 0;
    }

    @Override
    public void executeProcess(String[] input) {
        int count = 0;
        if (state.getTableProvider() != null) {
            if (state.getTable() == null) {
                System.out.println("no table");
                return;
            }
            count = state.getTable().rollback();
            System.out.println(count);
        }
    }
}
