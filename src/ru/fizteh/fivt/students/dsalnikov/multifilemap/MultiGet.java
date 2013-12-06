package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.students.dsalnikov.filemap.FileMap;
import ru.fizteh.fivt.students.dsalnikov.filemap.GetCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.Command;

import java.io.IOException;

public class MultiGet implements Command {

    @Override
    public void execute(Object state, String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("wrong usage of command get: 1 argument expected");
        } else {
            MultiFileMapState mfms = (MultiFileMapState) state;
            if (!mfms.getFlag()) {
                System.out.println("no table is loaded");
            } else {
                int hash = args[1].hashCode();
                int ndirectory = Math.abs(hash) % 16;
                int nfile = Math.abs(hash) / 16 % 16;
                FileTable temp = mfms.getTable();
                FileMap temptable = temp.getFileMap(String.valueOf(ndirectory) + ".dir", String.valueOf(nfile) + ".dat");
                if (temptable == null) {
                    System.out.println("not found " + args[1]);
                } else {
                    GetCommand gc = new GetCommand();
                    gc.execute(temptable.getState(), args);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
