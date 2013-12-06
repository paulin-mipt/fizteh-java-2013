package ru.fizteh.fivt.students.msandrikova.filemap;

import ru.fizteh.fivt.students.msandrikova.multifilehashmap.State;
import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.ExitCommand;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class Launcher {
    private static Command[] commands = new Command[] {
        new PutCommand(),
        new GetCommand(),
        new RemoveCommand(),
        new ExitCommand()
    };
    

    public static void main(String[] args) {
        String currentDirectory = System.getProperty("fizteh.db.dir");
        if (currentDirectory == null) {
            Utils.generateAnError("Incorrect work getProperty().", "Launcher", false);
        }
        State myState = new State(false, false, currentDirectory);
        Shell myShell = new Shell(commands, currentDirectory);
        myShell.setState(myState);
        myShell.execute(args);
    }

}
