package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;
import ru.fizteh.fivt.students.kislenko.storeable.StoreableState;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class CommandCreate implements Command<MultiTableFatherState> {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public int getArgCount() {
        if (MultiTableFatherState.class.isAssignableFrom((StoreableState.class))) {
            return -1;
        }
        return 1;
    }

    @Override
    public void run(MultiTableFatherState state, String[] args) throws Exception {
        AtomicReference<Exception> exception = new AtomicReference<Exception>();
        AtomicReference<String> message = new AtomicReference<String>();
        if (!state.alrightCreate(args[0], exception, message)) {
            System.out.println(message.get());
            throw exception.get();
        }

        File db = state.getPath().resolve(args[0]).toFile();
        if (db.exists()) {
            System.out.println(args[0] + " exists");
        } else {
            db.mkdir();
            try {
                state.createTable(args);
            } catch (Exception e) {
                db.delete();
                throw e;
            }
            System.out.println("created");
        }
    }
}
