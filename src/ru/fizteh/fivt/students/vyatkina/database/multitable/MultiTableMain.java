package ru.fizteh.fivt.students.vyatkina.database.multitable;

import ru.fizteh.fivt.students.vyatkina.Command;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseAdapter;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;
import ru.fizteh.fivt.students.vyatkina.database.StringDatabaseAdapter;
import ru.fizteh.fivt.students.vyatkina.database.commands.*;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderConstants;
import ru.fizteh.fivt.students.vyatkina.shell.Shell;

import java.util.HashSet;
import java.util.Set;

public class MultiTableMain {

    static Set<Command> getMultiHashFileMapCommands(DatabaseState state) {
        Set commands = new HashSet();
        commands.add(new GetCommand(state));
        commands.add(new PutCommand(state));
        commands.add(new RemoveCommand(state));
        commands.add(new ExitDatabaseCommand(state));
        commands.add(new CreateCommand(state));
        commands.add(new DropCommand(state));
        commands.add(new UseCommand(state));
        commands.add(new CommitCommand(state));
        commands.add(new SizeCommand(state));
        commands.add(new RollbackCommand(state));

        return commands;
    }

    public static void main(String[] args) {
        MultiTableProviderFactory multiTableProviderFactory = new MultiTableProviderFactory();
        String location = System.getProperty(TableProviderConstants.PROPERTY_DIRECTORY);

        MultiTableProvider tableProvider = null;
        try {
            tableProvider = MultiTableProvider.class.cast(multiTableProviderFactory.create(location));
        }
        catch (IllegalArgumentException e) {
            System.err.print(e.getMessage());
            System.exit(-1);
        }

        DatabaseAdapter databaseAdapter = new StringDatabaseAdapter(tableProvider, null);
        DatabaseState state = new DatabaseState(databaseAdapter);
        Set<Command> commands = getMultiHashFileMapCommands(state);
        Shell shell;

        if (args.length == 0) {
            shell = new Shell(commands, Shell.Mode.INTERACTIVE, state);
        } else {
            shell = new Shell(commands, Shell.Mode.PACKET, state);
        }
        shell.startWork(args);
    }
}
