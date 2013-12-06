package ru.fizteh.fivt.students.kislenko.filemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.util.concurrent.atomic.AtomicReference;

public class CommandPut implements Command<FatherState> {
    @Override
    public String getName() {
        return "put";
    }

    @Override
    public int getArgCount() {
        return 2;
    }

    @Override
    public void run(FatherState state, String[] args) throws Exception {
        AtomicReference<Exception> exception = new AtomicReference<Exception>(null);
        CommandUtils.assertStartingStateIsAlright(state);

        String value = state.get(args[0], exception);
        CommandUtils.assertWorkIsSuccessful(exception.get());

        state.put(args[0], args[1], exception);
        CommandUtils.assertWorkIsSuccessful(exception.get());
        if (value == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite\n" + value);
        }
    }
}
