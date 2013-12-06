package ru.fizteh.fivt.students.vyatkina;


public class CommandExecutionException extends RuntimeException {

    public CommandExecutionException() {
        super();
    }

    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandExecutionException(Throwable cause) {
        super(cause);
    }
}
