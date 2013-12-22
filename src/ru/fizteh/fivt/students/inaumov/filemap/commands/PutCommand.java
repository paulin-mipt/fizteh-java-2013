package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;

public class PutCommand<Table, Key, Value, State extends FileMapShellState<Table, Key, Value>>
        extends AbstractCommand<State> {
    public PutCommand() {
        super("put", 2);
    }

    public void execute(String argumentsLine, State state) {
        String[] arguments = state.parsePutCommand(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        Key key = state.parseKey(arguments[0]);
        Value value = state.parseValue(arguments[1]);
        Value oldValue = state.put(key, value);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(state.valueToString(oldValue));
        }
    }
}
