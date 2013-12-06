package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.students.ryabovaMaria.shell.Shell;

public class FileMap {
    public static File curDir;
    public static Shell shell;
    
    public static void main(String[] args) {
        try {
            FileMapCommands commands = new FileMapCommands("fizteh.db.dir");
            shell = new Shell(commands);
            if (args.length > 0) {
                shell.packet(args);
            } else {
             shell.interactive();
            }
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
