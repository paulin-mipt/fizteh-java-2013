package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.students.dsalnikov.filemap.FileMap;

import java.io.IOException;

public class MultiFileMapState {
    String state;
    public String workingdirectory;
    boolean usingtable;


    private FileTable currusingtable;

    public MultiFileMapState(String workingdir) throws IOException {
        usingtable = false;
        workingdirectory = workingdir;
    }

    public String getState() {
        return state;
    }

    public boolean getFlag() {
        return usingtable;
    }

    public void usingTable(FileTable t) {
        usingtable = true;
        currusingtable = t;
    }

    public void notUsingTable() {
        usingtable = false;
        currusingtable = null;
    }

    public String getWorkingDirectory() {
        return workingdirectory;
    }

    public String setWorkingDirectory(String newdirectory) {
        return workingdirectory = newdirectory;
    }

    public FileTable getTable() {
        return currusingtable;
    }

    public void insertIntoTable(String ndirectory, String nfile, FileMap ifile) throws IOException {
        currusingtable.insert(ndirectory, nfile, ifile);
    }

    public String getTableWd() {
        return currusingtable.getState();
    }
}
