package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;

public class CreateCommand<Table, Key, Value, State extends MultiFileMapShellState<Table, Key, Value>>
        extends AbstractCommand<State> {
    public CreateCommand() {
        super("create", 1);
    }

    public void execute(String argumentsLine, State state) {
        String[] arguments = state.parseCreateCommand(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        Table newTable = null;

        try {
            newTable = state.createTable(arguments[0]);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(arguments[0].split("\\s+")[0] + " exists");
        } else {
            System.out.println("created");
        }
    }
}
