package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.students.dsalnikov.filemap.FileMap;
import ru.fizteh.fivt.students.dsalnikov.filemap.RemoveCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.Command;

import java.io.IOException;

public class MultiRemove implements Command {

    @Override
    public void execute(Object state, String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("wrong usage of command remove: 1 argument expected");
        } else {
            MultiFileMapState mfhs = (MultiFileMapState) state;
            if (!mfhs.getFlag()) {
                System.out.println("no table is loaded");
            } else {
                int hashcode = args[1].hashCode();
                int ndirectory = Math.abs(hashcode) % 16;
                int nfile = Math.abs(hashcode) / 16 % 16;
                MultiFileMapState mfms = (MultiFileMapState) state;
                FileTable temp = mfms.getTable();
                FileMap temptable = temp.getFileMap(String.valueOf(ndirectory) + ".dir", String.valueOf(nfile) + ".dat");
                if (temptable == null) {
                    System.out.println("not found");
                    return;
                }
                RemoveCommand rc = new RemoveCommand();
                rc.execute(temptable.getState(), args);
            }
        }
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
