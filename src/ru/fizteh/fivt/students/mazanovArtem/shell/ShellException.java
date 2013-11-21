package ru.fizteh.fivt.students.mazanovArtem.shell;

class ShellException extends Exception {
    private final String action;
    private final String message;

    ShellException(String act, String mes) {
        action = act;
        message = mes;
    }

    @Override
    public String toString() {
        return action + ": " + message;
    }

}
