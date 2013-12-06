package ru.fizteh.fivt.students.asaitgalin.multifilehashmap;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands.*;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.extensions.ChangesCountingTableProviderFactory;
import ru.fizteh.fivt.students.asaitgalin.shell.CommandTable;
import ru.fizteh.fivt.students.asaitgalin.shell.ShellUtils;
import ru.fizteh.fivt.students.asaitgalin.shell.commands.ExitCommand;

public class Main {
    public static void main(String[] args) {
        CommandTable table = new CommandTable();

        ChangesCountingTableProviderFactory factory = new MultiFileTableProviderFactory();
        MultiFileTableState state = new MultiFileTableState();

        try {
            state.provider = factory.create(System.getProperty("fizteh.db.dir"));
        } catch (IllegalArgumentException iae) {
            System.err.println("no dir provided");
            System.exit(-1);
        }

        table.appendCommand(new PutCommand(state));
        table.appendCommand(new GetCommand(state));
        table.appendCommand(new RemoveCommand(state));
        table.appendCommand(new CreateCommand(state));
        table.appendCommand(new DropCommand(state));
        table.appendCommand(new UseCommand(state));
        table.appendCommand(new SizeCommand(state));
        table.appendCommand(new CommitCommand(state));
        table.appendCommand(new RollbackCommand(state));
        table.appendCommand(new ExitCommand());

        ShellUtils shellUtils = new ShellUtils(table);
        if (args.length == 0) {
            shellUtils.interactiveMode(System.in, System.out, System.err);
        } else {
            shellUtils.batchMode(args, System.err);
        }
    }
}
