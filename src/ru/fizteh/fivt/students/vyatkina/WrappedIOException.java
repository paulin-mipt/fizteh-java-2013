package ru.fizteh.fivt.students.vyatkina;


public class WrappedIOException extends RuntimeException {

    public WrappedIOException() {
        super();
    }

    public WrappedIOException(String message) {
        super(message);
    }

    public WrappedIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrappedIOException(Throwable cause) {
        super(cause);
    }

}
