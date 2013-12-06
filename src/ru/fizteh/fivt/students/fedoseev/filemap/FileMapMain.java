package ru.fizteh.fivt.students.fedoseev.filemap;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            File file = new File(System.getProperty("fizteh.db.dir"));

            file = file.getCanonicalFile().toPath().resolve("db.dat").toFile();

            AbstractFileMap fileMap = new AbstractFileMap(file);

            if (args.length != 0) {
                fileMap.batchMode(args);
            } else {
                fileMap.interactiveMode();
            }
        } catch (NullPointerException e) {
            System.err.println("ERROR: cannot get property\n");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: incorrect file\n");
            System.exit(1);
        }

        System.exit(0);
    }
}
