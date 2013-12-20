package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.students.vlmazlov.generics.commands.*;
import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.*;
import ru.fizteh.fivt.students.vlmazlov.storeable.commands.CreateStoreableCommand;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        StoreableTableProviderFactory factory = new StoreableTableProviderFactory();
        DataBaseState<Storeable, StoreableTable> state = null;

        try {
            state = new DataBaseState<Storeable, StoreableTable>(factory.create(System.getProperty("fizteh.db.dir")));
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(2);
        }

        try {
            ((StoreableTableProvider) state.getProvider()).read();
        } catch (IOException ex) {
            System.err.println("Unable to retrieve database: " + ex.getMessage());
            System.exit(3);
        } catch (ValidityCheckFailedException ex) {
            System.err.println("Validity check failed: " + ex.getMessage());
            System.exit(4);
        }

        Command[] commands = {
                new GetCommand(), new PutCommand(),
                new RemoveCommand(), new ExitCommand(),
                new UseCommand(), new CreateStoreableCommand(),
                new DropCommand(), new CommitCommand(),
                new RollBackCommand(), new SizeCommand()
        };

        Shell<DataBaseState> shell = new Shell<DataBaseState>(commands, state);

        try {
            shell.process(args);
        } catch (WrongCommandException ex) {
            System.err.println(ex.getMessage());
            System.exit(5);
        } catch (CommandFailException ex) {
            System.err.println(ex.getMessage());
            System.exit(6);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(7);
        } catch (UserInterruptionException ex) {
            //Do nothing
        }

        try {
            ((StoreableTableProvider) state.getProvider()).write();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(8);
        } catch (ValidityCheckFailedException ex) {
            System.err.println("Validity check failed: " + ex.getMessage());
            System.exit(9);
        }
    }
}
