package ru.fizteh.fivt.students.asaitgalin.storable;

public class BadSignatureFileException extends RuntimeException {

    public BadSignatureFileException() {
        super();
    }

    public BadSignatureFileException(String message) {
        super(message);
    }

    public BadSignatureFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadSignatureFileException(Throwable cause) {
        super(cause);
    }

}
