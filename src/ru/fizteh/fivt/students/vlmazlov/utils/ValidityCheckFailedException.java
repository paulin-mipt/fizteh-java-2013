package ru.fizteh.fivt.students.vlmazlov.utils;

public class ValidityCheckFailedException extends Exception {
    public ValidityCheckFailedException() {
        super();
    }

    public ValidityCheckFailedException(String message) {
        super(message);
    }

    public ValidityCheckFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidityCheckFailedException(Throwable cause) {
        super(cause);
    }
}
