package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

import ru.fizteh.fivt.students.dmitryKonturov.dataBase.shellEnvironment.StoreableFileMapShell;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMap {

    public static void main(String[] args) {
        String dbDir = System.getProperty("fizteh.db.dir");
        //String dbDir = "/home/kontr/testDir/myTest";
        if (dbDir == null) {
            System.err.println("Empty property");
            System.exit(1);
        }

        StoreableFileMapShell shell = null;
        try {
            Path dbDirPath = Paths.get(dbDir);
            if (Files.notExists(dbDirPath)) {
                Path parentPath = dbDirPath.getParent();
                if (Files.isDirectory(parentPath)) {
                    Files.createDirectory(dbDirPath);
                }
            }
            shell = new StoreableFileMapShell(dbDirPath);
        } catch (IOException|IllegalArgumentException e) {
            System.err.println("Unable to launch shell:  " + e.toString());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Wrong property: " + e.toString());
            System.exit(1);
        }

        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg);
                builder.append(" ");
            }
            try {
                shell.packageMode(builder.toString());
            } catch (ShellException e) {
                System.err.println(ShellEmulator.getNiceMessage(e));
                System.exit(1);
            }
        } else {
            shell.interactiveMode();
        }
    }
}
