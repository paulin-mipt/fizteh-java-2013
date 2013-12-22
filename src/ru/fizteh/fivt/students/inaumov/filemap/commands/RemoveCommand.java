package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

public class RemoveCommand<Table, Key, Value, State extends FileMapShellState<Table, Key, Value>>
        extends AbstractCommand<State> {
    public RemoveCommand() {
        super("remove", 1);
    }

    public void execute(String argumentsLine, State state) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        Key key = state.parseKey(arguments[0]);
        Value value = state.remove(key);

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
