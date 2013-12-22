package ru.fizteh.fivt.students.vlmazlov.shell;

public class CommandFailException extends Exception {
    public CommandFailException() {
        super();
    }

    public CommandFailException(String message) {
        super(message);
    }

    public CommandFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandFailException(Throwable cause) {
        super(cause);
    }
}
