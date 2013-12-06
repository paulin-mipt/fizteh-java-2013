package ru.fizteh.fivt.students.dmitryKonturov.shell;

public class ShellException extends Exception {

    private static String getUnitedMessage(String message, String reason) {
        if (message == null) {
            message = reason;
            reason = null;
        }
        if (reason == null) {
            if (message == null) {
                return "Database Exception";
            } else {
                return message;
            }
        } else {
            return message + ": " + reason;
        }
    }

    public ShellException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ShellException(String message, String reason) {
        super(getUnitedMessage(message, reason));
    }

    public ShellException(String message) {
        super(getUnitedMessage(message, null));
    }

    public ShellException() {
        super();
    }

    public ShellException(Throwable throwable) {
        super(throwable);
    }
}
