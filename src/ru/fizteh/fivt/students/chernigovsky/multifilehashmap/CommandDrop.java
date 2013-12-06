package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.FileMapState;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;


public class CommandDrop implements Command {
    public String getName() {
        return "drop";
    }
    public int getArgumentsCount() {
        return 1;
    }

    public void execute(State state, String[] args) throws IOException, ExitException {
        state.checkDropTableUsing(args[1]);
        if (!state.removeTable(args[1])) {
            System.out.println(args[1] + " not exists");
            return;
        }

        System.out.println("dropped");
    }

}
