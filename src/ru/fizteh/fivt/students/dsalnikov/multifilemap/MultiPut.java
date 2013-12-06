package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.students.dsalnikov.filemap.FileMap;
import ru.fizteh.fivt.students.dsalnikov.filemap.FileMapState;
import ru.fizteh.fivt.students.dsalnikov.filemap.PutCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.Command;

import java.io.File;
import java.io.IOException;

public class MultiPut implements Command {

    @Override
    public void execute(Object state, String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("wrong usage of command put: 2 arguments expected");
        } else {
            MultiFileMapState mfms = (MultiFileMapState) state;
            if (!mfms.getFlag()) {
                System.out.println("no table is loaded.");
                return;
            } else {
                int hashcode = args[1].hashCode();
                int ndirectory = Math.abs(hashcode) % 16;
                int nfile = Math.abs(hashcode) / 16 % 16;
                FileTable temp = mfms.getTable();
                FileMap temptable = temp.getFileMap(String.valueOf(ndirectory) + ".dir", String.valueOf(nfile) + ".dat");
                PutCommand pc = new PutCommand();
                if (temptable == null) {
                    File f = new File(mfms.getTableWd(), String.valueOf(ndirectory) + ".dir");
                    if (!f.exists()) {
                        //    f.mkdir();
                        f = new File(f.getAbsolutePath(), nfile + ".dat");
                        //  f.createNewFile();
                        FileMapState fmstate = new FileMapState(f.getAbsolutePath());
                        FileMap fm = new FileMap();
                        fm.setState(fmstate);
                        pc.execute(fm.getState(), args);
                        mfms.insertIntoTable(String.valueOf(ndirectory) + ".dir", String.valueOf(nfile) + ".dat", fm);
                    } else {
                        f = new File(f.getAbsolutePath(), nfile + ".dat");
                        if (!f.exists()) {
                            //f.createNewFile();
                            FileMapState fmstate = new FileMapState(f.getAbsolutePath());
                            FileMap fm = new FileMap();
                            fm.setState(fmstate);
                            pc.execute(fm.getState(), args);
                            mfms.insertIntoTable(String.valueOf(ndirectory) + ".dir", String.valueOf(nfile) + ".dat", fm);
                        }
                    }
                } else {
                    pc.execute(temptable.getState(), args);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "put";
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
