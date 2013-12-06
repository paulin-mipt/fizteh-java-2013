package ru.fizteh.fivt.students.dsalnikov.filemap;


import ru.fizteh.fivt.students.dsalnikov.shell.Command;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Scanner;


public class main {

    public static void main(String[] args) throws IOException {

        String s = System.getProperty("fizteh.db.dir");
        if (s == null) {
            System.exit(-1);
        } else {
            File f = new File(s);
            if (!f.isDirectory()) {
                System.exit(-1);
            }
        }

        FileMapState f = new FileMapState(s);
        FileMap main = new FileMap(f);
        Shell shell = new Shell(f);


        f.build(f.getState());
        FileMap filemap = new FileMap(f);

        ArrayList<Command> Commands = new ArrayList();

        GetCommand gc = new GetCommand();
        Commands.add(gc);

        ExitCommand ec = new ExitCommand();
        Commands.add(ec);

        PutCommand pc = new PutCommand();
        Commands.add(pc);

        RemoveCommand rc = new RemoveCommand();
        Commands.add(rc);

        shell.setCommands(Commands);

        if (args.length == 0) {
            shell.batchMode();
        } else {
            shell.commandMode(args);
        }
    }
}

