package ru.fizteh.fivt.students.elenav.commands;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.utils.ExitException;

public class ExitCommand extends AbstractCommand {
    public ExitCommand(FilesystemState s) {
        super(s, "exit", 0);
    }
    
    public void execute(String[] args) {
        getState().commit();
        throw new ExitException();
    }
}
