package ru.fizteh.fivt.students.kislenko.filemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.util.concurrent.atomic.AtomicReference;

public class CommandRemove implements Command<FatherState> {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public int getArgCount() {
        return 1;
    }

    @Override
    public void run(FatherState state, String[] args) throws Exception {
        AtomicReference<Exception> exception = new AtomicReference<Exception>(null);
        CommandUtils.assertStartingStateIsAlright(state);

        String value = state.get(args[0], exception);
        CommandUtils.assertWorkIsSuccessful(exception.get());

        if (value == null) {
            System.out.println("not found");
        } else {
            state.remove(args[0], exception);
            CommandUtils.assertWorkIsSuccessful(exception.get());
            System.out.println("removed\n" + value);
        }
    }
}
