package ru.fizteh.fivt.students.vyatkina.database.logging;


import java.util.concurrent.atomic.AtomicBoolean;

public class CloseState {

    private AtomicBoolean isClosed = new AtomicBoolean(false);
    private final String StandardCloseMessage;

    public CloseState () {
        StandardCloseMessage = "The object is closed";
    }

    public CloseState (String standardCloseMessage) {
        this.StandardCloseMessage = standardCloseMessage;
    }

    public void close () {
        isClosed.set(true);
    }

    public boolean isAlreadyClosed () {
       return isClosed.get();
    }

    public void isClosedCheck() {
        if (isClosed.get()) {
            throw new IllegalStateException(StandardCloseMessage);
        }
    }

    public void isClosedCheck(String message) {
        if (isClosed.get()) {
            throw new IllegalStateException(message);
        }
    }

}
