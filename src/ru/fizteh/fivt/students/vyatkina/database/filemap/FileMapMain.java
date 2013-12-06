package ru.fizteh.fivt.students.vyatkina.database.filemap;

import ru.fizteh.fivt.students.vyatkina.Command;
import ru.fizteh.fivt.students.vyatkina.TimeToFinishException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseAdapter;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;
import ru.fizteh.fivt.students.vyatkina.database.StringDatabaseAdapter;
import ru.fizteh.fivt.students.vyatkina.database.commands.ExitDatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.GetCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.PutCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.RemoveCommand;
import ru.fizteh.fivt.students.vyatkina.shell.Shell;

import java.util.HashSet;
import java.util.Set;

public class FileMapMain {

    static Set<Command> getFileMapCommands(DatabaseState state) {
        Set commands = new HashSet();
        commands.add(new GetCommand(state));
        commands.add(new PutCommand(state));
        commands.add(new RemoveCommand(state));
        commands.add(new ExitDatabaseCommand(state));
        return commands;
    }

    public static void main(String[] args) {

        try {
            FileMapTable fileMapTable = new FileMapTable("LittleFilemapTable");
            FileMapTableProvider tableProvider = new FileMapTableProvider(fileMapTable);
            DatabaseAdapter databaseAdapter = new StringDatabaseAdapter(tableProvider, fileMapTable);
            DatabaseState state = new DatabaseState(databaseAdapter);

            Set<Command> commands = getFileMapCommands(state);

            Shell shell;
            if (args.length == 0) {
                shell = new Shell(commands, Shell.Mode.INTERACTIVE, state);
            } else {
                shell = new Shell(commands, Shell.Mode.PACKET, state);
            }
            shell.startWork(args);
        }
        catch (TimeToFinishException e) {
            Thread.currentThread().isInterrupted();
        }

    }

}
