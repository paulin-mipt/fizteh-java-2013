package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.ExitException;;

@SuppressWarnings("serial")
class DbExitException extends ExitException {
    public DbExitException() {
        super();
    }
        
    public DbExitException(String text) {
        super(text);
    }
        
    public DbExitException(int status) {
        super(Integer.toString(status));         
    }
}
