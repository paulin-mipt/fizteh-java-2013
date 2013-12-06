package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.State;
import ru.fizteh.fivt.students.vyatkina.TimeToFinishException;

public class ExitCommand extends AbstractCommand<State> {

    public ExitCommand(State state) {
        super(state);
        this.name = "exit";
        this.argsCount = 0;
    }

    @Override
    public void execute(String[] args) {
        throw new TimeToFinishException();
    }
}
