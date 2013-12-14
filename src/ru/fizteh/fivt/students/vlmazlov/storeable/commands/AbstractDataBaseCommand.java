package ru.fizteh.fivt.students.vlmazlov.storeable.commands;

import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableDataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;

public abstract class AbstractDataBaseCommand extends AbstractCommand<StoreableDataBaseState> {
    public AbstractDataBaseCommand(String name, int argNum) {
        super(name, argNum);
    }
}
