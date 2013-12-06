package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.students.dsalnikov.shell.Command;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

        MultiFileMapState mfhm = new MultiFileMapState(s);
        Shell sh = new Shell(mfhm);

        ArrayList<Command> Commands = new ArrayList<Command>();

        MultiGet mg = new MultiGet();
        Commands.add(mg);

        MultiPut mp = new MultiPut();
        Commands.add(mp);

        MultiRemove mr = new MultiRemove();
        Commands.add(mr);

        DropCommand dc = new DropCommand();
        Commands.add(dc);

        CreateNewTableCommand cntc = new CreateNewTableCommand();
        Commands.add(cntc);

        UseCommand uc = new UseCommand();
        Commands.add(uc);

        MultiExitCommand mec = new MultiExitCommand();
        Commands.add(mec);

        sh.setCommands(Commands);

        if (args.length == 0) {
            sh.batchMode();
        } else {
            sh.commandMode(args);
        }


    }
}
