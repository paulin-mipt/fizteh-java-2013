package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

/**
 * Throws in work with my databases.
 */

public class DatabaseException extends Exception {
    private final int nesting;

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

    @Override
    public String toString() {
        String current = super.toString();
        String[] strings = current.split(":", 2);
        if (strings.length > 1) {
            if (strings[1] != null) {
                if (!strings[1].equals("")) {
                    return strings[1];
                }
            }
        }
        return current;
    }

    public String getDatabaseExceptionLongMessage() {
        if (nesting > 0) {
            DatabaseException dbe = (DatabaseException) getCause();
            return getUnitedMessage(getMessage(), dbe.getDatabaseExceptionLongMessage());
        } else {
            Throwable throwable = getCause();
            if (throwable != null) {
                return getUnitedMessage(getMessage(), throwable.toString());
            } else {
                return getMessage();
            }
        }
    }

    public DatabaseException(String message, Throwable throwable) {
        super(message, throwable);
        if (throwable instanceof DatabaseException) {
            DatabaseException dbe = (DatabaseException) throwable;
            nesting = dbe.nesting + 1;
        } else {
            nesting = 0;
        }
    }

    public DatabaseException(String message, String reason) {
        super(getUnitedMessage(message, reason));
        nesting = 0;
    }

    public DatabaseException(String message) {
        super(getUnitedMessage(message, null));
        nesting = 0;
    }

    public DatabaseException() {
        super();
        nesting = 0;
    }

    public DatabaseException(Throwable throwable) {
        super(throwable);
        nesting = 0;
    }
}
