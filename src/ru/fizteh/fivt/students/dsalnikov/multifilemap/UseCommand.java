package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.students.dsalnikov.shell.Command;

import java.io.File;
import java.io.IOException;

public class UseCommand implements Command {


    @Override
    public void execute(Object state, String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("wrong usage of command use: 1 argument expected");
        }
        MultiFileMapState mfms = (MultiFileMapState) state;
        File f = new File(mfms.workingdirectory, args[1]);
        if (!f.exists()) {
            System.out.println("table " + args[1] + " not exists");
        } else {
            if (!mfms.getFlag()) {
                FileTable temp = new FileTable(f.getAbsolutePath());
                mfms.usingTable(temp);
                System.out.println("using " + f.getName());
            } else {
                if (f.getCanonicalPath() == args[1]) {
                    System.out.println("This table is already loaded");
                    return;
                } else {
                    FileTable ttable = mfms.getTable();
                    ttable.flush();
                    FileTable newtable = new FileTable(f.getAbsolutePath());
                    mfms.usingTable(newtable);
                    System.out.println("using " + f.getName());
                }
            }
        }
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
