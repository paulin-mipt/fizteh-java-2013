package ru.fizteh.fivt.students.elenav.commands;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class RollbackCommand  extends AbstractCommand {

    public RollbackCommand(FilesystemState s) {
        super(s, "rollback", 0);
    }

    @Override
    public void execute(String[] args) {
        getState().getStream().println(getState().rollback());
    }

}
