package ru.fizteh.fivt.students.krivchansky.shell;


public final class SomethingIsWrongException extends Exception {
    public SomethingIsWrongException() { 
        super(); 
    }
    
    public SomethingIsWrongException(String message) { 
        super(message); 
    }
    
    public SomethingIsWrongException(String message, Throwable cause) { 
        super(message, cause); 
    }
    
    public SomethingIsWrongException(Throwable cause) { 
        super(cause); 
    }
}
