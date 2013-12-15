package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

public class UseCommand<Table, Key, Value, State extends MultiFileMapShellState<Table, Key, Value>>
        extends AbstractCommand<State> {
    public UseCommand() {
        super("use", 1);
    }

    public void execute(String argumentsLine, State state) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        Table newTable = null;

        try {
            newTable = state.useTable(arguments[0]);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(arguments[0] + " not exists");
            return;
        }

        System.out.println("using " + state.getCurrentTableName());
    }
}
