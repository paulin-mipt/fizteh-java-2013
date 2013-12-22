package ru.fizteh.fivt.students.vlmazlov.shell;

public class UserInterruptionException extends Exception {
    public UserInterruptionException() {
        super();
    }

    public UserInterruptionException(String message) {
        super(message);
    }

    public UserInterruptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserInterruptionException(Throwable cause) {
        super(cause);
    }
}
