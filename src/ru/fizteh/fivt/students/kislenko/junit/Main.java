package ru.fizteh.fivt.students.kislenko.junit;

import ru.fizteh.fivt.students.kislenko.filemap.CommandGet;
import ru.fizteh.fivt.students.kislenko.filemap.CommandPut;
import ru.fizteh.fivt.students.kislenko.filemap.CommandRemove;
import ru.fizteh.fivt.students.kislenko.multifilemap.CommandCreate;
import ru.fizteh.fivt.students.kislenko.multifilemap.CommandDrop;
import ru.fizteh.fivt.students.kislenko.multifilemap.CommandUse;
import ru.fizteh.fivt.students.kislenko.shell.Command;
import ru.fizteh.fivt.students.kislenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        String dbAddress = System.getProperty("fizteh.db.dir");
        if (dbAddress == null) {
            System.err.println("Set database directory before start.");
            System.exit(-1);
        }
        try {
            File dbDir = new File(dbAddress).getCanonicalFile();
            if (!dbDir.isDirectory()) {
                System.err.println("Incorrect database directory.");
                System.exit(1);
            }
            File[] tables = dbDir.listFiles();
            if (tables != null) {
                for (File table : tables) {
                    if (!table.isDirectory()) {
                        System.err.println("Incorrect table format.");
                        System.exit(1);
                    }
                }
            }
            Path db = dbDir.toPath();
            JUnitState state = new JUnitState(db);
            Command[] commandList = new Command[]{new CommandRemove(), new CommandPut(), new CommandCreate(),
                    new CommandDrop(), new CommandGet(), new CommandUse(), new CommandSize(),
                    new CommandCommit(), new CommandRollback()};
            MultiFilemapBuilder builder = new MultiFilemapBuilder();
            builder.build(state);
            Shell<JUnitState> shell = new Shell<JUnitState>(state, commandList);
            if (args.length == 0) {
                shell.interactiveMode();
            } else {
                shell.batchMode(args);
            }
            builder.finish(state);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}
