package ru.fizteh.fivt.students.vyatkina.database.storable;

import ru.fizteh.fivt.students.vyatkina.Command;
import ru.fizteh.fivt.students.vyatkina.TimeToFinishException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseAdapter;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;
import ru.fizteh.fivt.students.vyatkina.database.StorableDatabaseAdapter;
import ru.fizteh.fivt.students.vyatkina.database.commands.*;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderConstants;
import ru.fizteh.fivt.students.vyatkina.shell.Shell;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StorableMain {

    static Set<Command> getStorableCommands(DatabaseState state) {
        Set commands = new HashSet();
        commands.add(new GetCommand(state));
        commands.add(new PutCommand(state));
        commands.add(new RemoveCommand(state));
        commands.add(new ExitDatabaseCommand(state));
        commands.add(new CreateStructedCommand(state));
        commands.add(new DropCommand(state));
        commands.add(new UseCommand(state));
        commands.add(new CommitCommand(state));
        commands.add(new SizeCommand(state));
        commands.add(new RollbackCommand(state));

        return commands;
    }

    public static void main(String[] args) {
        StorableTableProviderFactory factory = new StorableTableProviderFactory();
        String location = System.getProperty(TableProviderConstants.PROPERTY_DIRECTORY);

        StorableTableProviderImp tableProvider = null;
        try {
            tableProvider = StorableTableProviderImp.class.cast(factory.create(location));
        }
        catch (IllegalArgumentException | IOException | WrappedIOException e) {
            System.err.print(e);
            System.exit(-1);
        }

        DatabaseAdapter databaseAdapter = new StorableDatabaseAdapter(tableProvider, null);
        DatabaseState state = new DatabaseState(databaseAdapter);
        Set<Command> commands = getStorableCommands(state);
        Shell shell;

        if (args.length == 0) {
            shell = new Shell(commands, Shell.Mode.INTERACTIVE, state);
        } else {
            shell = new Shell(commands, Shell.Mode.PACKET, state);
        }


        try {
            shell.startWork(args);
        }
        catch (TimeToFinishException e) {
            if (e.getMessage() == null) {
                System.exit(0);
            } else {
                System.exit(-1);
            }
        }

        finally {
            try {
                factory.close();
            }
            catch (IOException e) {
                System.err.print(e);
                System.exit(-1);
            }
        }
    }

}
