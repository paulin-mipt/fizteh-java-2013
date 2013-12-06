package ru.fizteh.fivt.students.msandrikova.storeable;

import ru.fizteh.fivt.students.msandrikova.filemap.GetCommand;
import ru.fizteh.fivt.students.msandrikova.filemap.PutCommand;
import ru.fizteh.fivt.students.msandrikova.filemap.RemoveCommand;
import ru.fizteh.fivt.students.msandrikova.multifilehashmap.CommitCommand;
import ru.fizteh.fivt.students.msandrikova.multifilehashmap.CreateCommand;
import ru.fizteh.fivt.students.msandrikova.multifilehashmap.DropCommand;
import ru.fizteh.fivt.students.msandrikova.multifilehashmap.RollbackCommand;
import ru.fizteh.fivt.students.msandrikova.multifilehashmap.SizeCommand;
import ru.fizteh.fivt.students.msandrikova.multifilehashmap.State;
import ru.fizteh.fivt.students.msandrikova.multifilehashmap.UseCommand;
import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.ExitCommand;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class Launcher {
    private static Command[] commands = new Command[] {
        new PutCommand(),
        new GetCommand(),
        new RemoveCommand(),
        new CreateCommand(),
        new DropCommand(),
        new UseCommand(),
        new ExitCommand(),
        new SizeCommand(),
        new RollbackCommand(),
        new CommitCommand()
    };

    public static void main(String[] args) {
        String currentDirectory = System.getProperty("fizteh.db.dir");
        if (currentDirectory == null) {
            Utils.generateAnError("Incorrect work getProperty().", "Launcher", false);
        }
        State myState = new State(false, true, currentDirectory);    
        Shell myShell = new Shell(commands, currentDirectory);
        myShell.setState(myState);
        myShell.execute(args);
    }

}
