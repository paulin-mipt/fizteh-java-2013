package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.students.surakshina.shell.Command;
import ru.fizteh.fivt.students.surakshina.shell.Shell;

public class DataBaseMain {
    public static void main(String[] args) {
        NewTableProviderFactory factory = new NewTableProviderFactory();
        String workingDirectory = System.getProperty("fizteh.db.dir");
        if (workingDirectory == null || workingDirectory.toString().trim().isEmpty()) {
            System.err.println("No directory");
            System.exit(1);
        }
        NewTableProvider provider = null;
        try {
            provider = (NewTableProvider) factory.create(workingDirectory);
            TableState state = new TableState(new File(workingDirectory), provider);
            Set<Command> commands = tableCommands(state);
            Shell shell = new Shell(state, commands);
            shell.startWork(args);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            try {
                factory.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public static Set<Command> tableCommands(TableState state) {
        Set<Command> tableCommands = new HashSet<Command>();
        tableCommands.add(new CommandCommit(state));
        tableCommands.add(new CommandRollback(state));
        tableCommands.add(new CommandGet(state));
        tableCommands.add(new CommandPut(state));
        tableCommands.add(new CommandRemove(state));
        tableCommands.add(new CommandExitSave(state));
        tableCommands.add(new CommandUse(state));
        tableCommands.add(new CommandCreate(state));
        tableCommands.add(new CommandDrop(state));
        tableCommands.add(new CommandSize(state));
        return tableCommands;

    }
}
