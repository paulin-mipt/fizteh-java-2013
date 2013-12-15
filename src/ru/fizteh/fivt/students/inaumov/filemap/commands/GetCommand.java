package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

public class GetCommand<Table, Key, Value, State extends FileMapShellState<Table, Key, Value>>
        extends AbstractCommand<State> {
    public GetCommand() {
        super("get", 1);
    }

    public void execute(String argumentsLine, State state) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        Key key = state.parseKey(arguments[0]);
        Value value = state.get(key);

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(state.valueToString(value));
        }
    }
}
