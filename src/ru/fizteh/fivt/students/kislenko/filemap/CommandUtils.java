package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class CommandUtils {
    public static void assertStartingStateIsAlright(FatherState state) throws Exception {
        AtomicReference<Exception> exception = new AtomicReference<Exception>(null);
        AtomicReference<String> message = new AtomicReference<String>(null);
        if (!state.alrightPutGetRemove(exception, message)) {
            System.out.println(message);
            throw exception.get();
        }
    }

    public static void assertWorkIsSuccessful(Exception exception) throws Exception {
        if (exception != null) {
            System.out.println(exception.getMessage());
            throw exception;
        }
    }

    public static boolean multiTablePutGetRemoveAlright(Object currentTable,
                                                        AtomicReference<Exception> checkingException,
                                                        AtomicReference<String> message) {
        if (currentTable == null) {
            message.set("no table");
            checkingException.set(new IOException("Database haven't initialized."));
            return false;
        }
        return true;
    }
}
