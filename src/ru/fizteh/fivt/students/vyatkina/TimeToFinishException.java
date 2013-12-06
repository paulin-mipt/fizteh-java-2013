package ru.fizteh.fivt.students.vyatkina;


public class TimeToFinishException extends RuntimeException {

    public static final String DEATH_MESSAGE = "Death error";

    public TimeToFinishException() {
        super();
    }

    public TimeToFinishException(String message) {
        super(message);
    }

    public TimeToFinishException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeToFinishException(Throwable cause) {
        super(cause);
    }
}
