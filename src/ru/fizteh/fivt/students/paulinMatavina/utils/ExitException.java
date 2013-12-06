package ru.fizteh.fivt.students.paulinMatavina.utils;

@SuppressWarnings("serial")
public class ExitException extends RuntimeException {
    public ExitException() {
        super();
    }
        
    public ExitException(String text) {
        super(text);
    }
        
    public ExitException(int status) {
        super(Integer.toString(status));         
    }
}

