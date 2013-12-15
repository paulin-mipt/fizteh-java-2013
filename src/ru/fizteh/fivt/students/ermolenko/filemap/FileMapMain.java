package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    public static void main(String[] args) throws IOException {

        String currentProperty = System.getProperty("fizteh.db.dir");
        if (currentProperty == null) {
            System.exit(-1);
        }
        File base = new File(currentProperty);
        try {
            if (!base.exists()) {
                base.createNewFile();
            }

            base = base.getCanonicalFile().toPath().resolve("db.dat").toFile();
            FileMapState startState = new FileMapState(base);
            FileMapUtils.readDataBase(startState);
            Shell<FileMapState> filemap = new Shell<FileMapState>(startState);

            FileMapExecutor exec = new FileMapExecutor();

            if (args.length > 0) {
                filemap.batchState(args, exec);
            } else {
                filemap.interactiveState(exec);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }
}
