package ru.fizteh.fivt.students.valentinbarishev.shell;

public class InvalidCommandException extends Error {
    public InvalidCommandException(final String message) {
        super(message);
    }
}
