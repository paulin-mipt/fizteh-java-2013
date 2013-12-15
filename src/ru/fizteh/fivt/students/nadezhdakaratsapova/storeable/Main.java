package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.students.nadezhdakaratsapova.commands.*;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Shell;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.StringMethods;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try {
            String dirName = System.getProperty("fizteh.db.dir");
            Shell storeableController = new Shell();
            StoreableTableProviderFactory providerFactory = new StoreableTableProviderFactory();
            StoreableTableProvider state = providerFactory.create(dirName);
            storeableController.addCommand(new CommitCommand(state));
            storeableController.addCommand(new CreateCommand(state));
            storeableController.addCommand(new DropCommand(state));
            storeableController.addCommand(new ExitCommand(state));
            storeableController.addCommand(new GetCommand(state));
            storeableController.addCommand(new PutCommand(state));
            storeableController.addCommand(new RemoveCommand(state));
            storeableController.addCommand(new RollbackCommand(state));
            storeableController.addCommand(new SizeCommand(state));
            storeableController.addCommand(new UseCommand(state));
            if (args.length == 0) {
                storeableController.interactiveMode();
            } else {
                String arguments = StringMethods.join(Arrays.asList(args), " ");
                try {
                    storeableController.batchMode(arguments);
                    if (state.curDataBaseStorage != null) {
                        state.curDataBaseStorage.writeToDataBase();
                    }
                } catch (IOException e) {
                    if (state.curDataBaseStorage != null) {
                        state.curDataBaseStorage.writeToDataBase();
                    }
                    System.err.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            System.exit(3);
        } catch (IndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            System.exit(4);
        }
    }
}
