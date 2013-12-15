package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.File;
import java.io.IOException;

public class StoreableMain {

    public static void main(String[] args) throws IOException {

        String currentProperty = System.getProperty("fizteh.db.dir");
        if (currentProperty == null) {
            System.exit(-1);
        }
        File base = new File(currentProperty);
        try {
            if (!base.exists()) {
                base.mkdir();
            }

            base = base.getCanonicalFile();
            StoreableState startState = new StoreableState(base);
            Shell<StoreableState> storeable = new Shell<StoreableState>(startState);

            StoreableExecutor exec = new StoreableExecutor();

            if (args.length > 0) {
                storeable.batchState(args, exec);
            } else {
                storeable.interactiveState(exec);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
