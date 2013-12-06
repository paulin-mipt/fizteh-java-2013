package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandSize extends DataBaseCommand {

    public CommandSize(TableState state) {
        super(state);
        name = "size";
        numberOfArguments = 0;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTable() != null) {
            System.out.println(state.getTable().size());
        } else {
            System.out.println("no table");
        }

    }

}
