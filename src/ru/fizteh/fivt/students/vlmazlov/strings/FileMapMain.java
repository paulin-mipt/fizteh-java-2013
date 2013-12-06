package ru.fizteh.fivt.students.vlmazlov.strings;

import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;
import ru.fizteh.fivt.students.vlmazlov.generics.commands.GetCommand;
import ru.fizteh.fivt.students.vlmazlov.generics.commands.PutCommand;
import ru.fizteh.fivt.students.vlmazlov.generics.commands.RemoveCommand;
import ru.fizteh.fivt.students.vlmazlov.shell.*;
import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    public static void main(String[] args) {
        DataBaseState<String, StringTable> state = null;
        StringTable table = null;
        File tempDir = null;
        try {
            tempDir = FileUtils.createTempDir("provider", null);
            if (tempDir == null) {
                System.err.println("Unable to create a temporary directory");
                System.exit(1);
            }
            StringTableProvider provider = new StringTableProvider(tempDir.getPath(), true);

            state = new DataBaseState(provider);
            table = new StringTable(provider, "table", true);

            state.setActiveTable(table);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (ValidityCheckFailedException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        try {
            table.read(System.getProperty("fizteh.db.dir"), "db.dat");
        } catch (IOException ex) {
            System.err.println("Unable to retrieve entries from file: " + ex.getMessage());
            System.exit(2);
        } catch (ValidityCheckFailedException ex) {
            System.err.println("Validity check failed: " + ex.getMessage());
            System.exit(3);
        }

        Command[] commands = {
                new PutCommand(), new GetCommand(),
                new RemoveCommand(), new ExitCommand()
        };

        Shell<DataBaseState> shell = new Shell<DataBaseState>(commands, state);

        try {
            shell.process(args);
        } catch (WrongCommandException ex) {
            System.err.println(ex.getMessage());
            System.exit(5);
        } catch (CommandFailException ex) {
            System.err.println("error while processing command: " + ex.getMessage());
            System.exit(6);
        } catch (UserInterruptionException ex) {
        }

        try {
            table.write(System.getProperty("fizteh.db.dir"), "db.dat");
        } catch (IOException ex) {
            System.err.println("Unable to store entries in the file: " + ex.getMessage());
            System.exit(7);
        } catch (ValidityCheckFailedException ex) {
            System.err.println("Validity check failed: " + ex.getMessage());
            System.exit(8);
        }

        System.exit(0);
    }
}
