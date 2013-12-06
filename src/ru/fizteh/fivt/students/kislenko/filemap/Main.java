package ru.fizteh.fivt.students.kislenko.filemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;
import ru.fizteh.fivt.students.kislenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        String dbAddress = System.getProperty("fizteh.db.dir");
        MapBuilder mb = new MapBuilder();
        try {
            Path db = new File(dbAddress).getCanonicalFile().toPath().resolve("db.dat");
            FilemapState state = new FilemapState(db);
            mb.buildMap(state);
            Command[] commandList = new Command[]{new CommandGet(), new CommandPut(), new CommandRemove()};
            Shell<FilemapState> shell = new Shell<FilemapState>(state, commandList);
            if (args.length == 0) {
                shell.interactiveMode();
            } else {
                shell.batchMode(args);
            }
            mb.fillFile(state);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}
