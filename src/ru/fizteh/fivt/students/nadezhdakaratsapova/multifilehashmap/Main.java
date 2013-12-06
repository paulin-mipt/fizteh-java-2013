package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.commands.*;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Shell;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.StringMethods;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            String dirName = System.getProperty("fizteh.db.dir");
            Shell multiFileHashMap = new Shell();
            MultiFileProviderFactory providerFactory = new MultiFileProviderFactory();
            MultiFileHashMapProvider state = providerFactory.create(dirName);
            multiFileHashMap.addCommand(new CommitCommand(state));
            multiFileHashMap.addCommand(new CreateCommand(state));
            multiFileHashMap.addCommand(new DropCommand(state));
            multiFileHashMap.addCommand(new ExitCommand(state));
            multiFileHashMap.addCommand(new GetCommand(state));
            multiFileHashMap.addCommand(new PutCommand(state));
            multiFileHashMap.addCommand(new RemoveCommand(state));
            multiFileHashMap.addCommand(new RollbackCommand(state));
            multiFileHashMap.addCommand(new SizeCommand(state));
            multiFileHashMap.addCommand(new UseCommand(state));
            if (args.length == 0) {
                multiFileHashMap.interactiveMode();
            } else {
                String arguments = StringMethods.join(Arrays.asList(args), " ");
                try {
                    multiFileHashMap.batchMode(arguments);
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
        }
    }
}
