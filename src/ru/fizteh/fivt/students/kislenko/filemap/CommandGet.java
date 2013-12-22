package ru.fizteh.fivt.students.kislenko.filemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.util.concurrent.atomic.AtomicReference;

public class CommandGet implements Command<FatherState> {

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public int getArgCount() {
        return 1;
    }

    @Override
    public void run(FatherState state, String[] args) throws Exception {
        AtomicReference<Exception> exception = new AtomicReference<Exception>();
        CommandUtils.assertStartingStateIsAlright(state);

        String value = state.get(args[0], exception);
        CommandUtils.assertWorkIsSuccessful(exception.get());

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + value);
        }
    }
}
