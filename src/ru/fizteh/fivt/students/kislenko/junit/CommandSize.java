package ru.fizteh.fivt.students.kislenko.junit;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandSize implements Command<TransactionalFatherState> {

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void run(TransactionalFatherState state, String[] args) throws IOException {
        if (!state.hasCurrentTable()) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }
        System.out.println(state.getCurrentTableSize());
    }
}
