package ru.fizteh.fivt.students.asaitgalin.storable;

import ru.fizteh.fivt.students.asaitgalin.shell.commands.ExitCommand;
import ru.fizteh.fivt.students.asaitgalin.storable.commands.*;
import ru.fizteh.fivt.students.asaitgalin.shell.CommandTable;
import ru.fizteh.fivt.students.asaitgalin.shell.ShellUtils;
import ru.fizteh.fivt.students.asaitgalin.storable.extensions.ExtendedTableProviderFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        CommandTable cmdTable = new CommandTable();

        ExtendedTableProviderFactory factory = new MultiFileTableProviderFactory();
        MultiFileTableState state = new MultiFileTableState();

        try {
            state.provider = factory.create(System.getProperty("fizteh.db.dir"));
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            System.exit(-1);
        } catch (IllegalArgumentException iea) {
            System.err.println("no dir provided");
            System.exit(-1);
        } catch (BadSignatureFileException bsfe) {
            System.err.println(bsfe.getMessage());
            System.exit(-1);
        }

        // Commands
        cmdTable.appendCommand(new PutCommand(state));
        cmdTable.appendCommand(new GetCommand(state));
        cmdTable.appendCommand(new RemoveCommand(state));
        cmdTable.appendCommand(new CreateCommand(state));
        cmdTable.appendCommand(new DropCommand(state));
        cmdTable.appendCommand(new UseCommand(state));
        cmdTable.appendCommand(new SizeCommand(state));
        cmdTable.appendCommand(new CommitCommand(state));
        cmdTable.appendCommand(new RollbackCommand(state));
        cmdTable.appendCommand(new ExitCommand());

        ShellUtils utils = new ShellUtils(cmdTable);
        if (args.length == 0) {
            utils.interactiveMode(System.in, System.out, System.err);
        } else {
            utils.batchMode(args, System.err);
        }
    }
}
