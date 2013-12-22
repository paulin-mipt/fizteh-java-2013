package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.students.kislenko.filemap.CommandGet;
import ru.fizteh.fivt.students.kislenko.filemap.CommandPut;
import ru.fizteh.fivt.students.kislenko.filemap.CommandRemove;
import ru.fizteh.fivt.students.kislenko.junit.CommandCommit;
import ru.fizteh.fivt.students.kislenko.junit.CommandRollback;
import ru.fizteh.fivt.students.kislenko.junit.CommandSize;
import ru.fizteh.fivt.students.kislenko.multifilemap.CommandCreate;
import ru.fizteh.fivt.students.kislenko.multifilemap.CommandDrop;
import ru.fizteh.fivt.students.kislenko.multifilemap.CommandUse;
import ru.fizteh.fivt.students.kislenko.shell.Command;
import ru.fizteh.fivt.students.kislenko.shell.Shell;

import java.io.File;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        String dbAddress = System.getProperty("fizteh.db.dir");
        if (dbAddress == null) {
            System.err.println("Set database directory before start.");
            System.exit(-1);
        }
        try {
            File dbDir = new File(dbAddress).getCanonicalFile();
            if (dbDir.isFile()) {
                System.err.println("Incorrect database directory.");
                System.exit(1);
            }
            if (!dbDir.exists()) {
                dbDir.mkdir();
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
            StoreableState state = new StoreableState(db);
            Command[] commandList = new Command[]{new CommandCommit(), new CommandCreate(), new CommandDrop(),
                    new CommandRollback(), new CommandSize(), new CommandGet(), new CommandRemove(),
                    new CommandPut(), new CommandUse()};
            StoreableBuilder builder = new StoreableBuilder();
            builder.build(state);
            Shell<StoreableState> shell = new Shell<StoreableState>(state, commandList);
            if (args.length == 0) {
                shell.interactiveMode();
            } else {
                shell.batchMode(args);
            }
            builder.finish(state);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}
