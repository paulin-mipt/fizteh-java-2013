package ru.fizteh.fivt.students.surakshina.filemap;

import ru.fizteh.fivt.students.surakshina.shell.AbstractCommand;

public abstract class DataBaseCommand extends AbstractCommand {
    protected TableState state;

    public DataBaseCommand(TableState state) {
        super(state);
        this.state = state;
    }
}
