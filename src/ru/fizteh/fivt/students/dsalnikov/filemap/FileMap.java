package ru.fizteh.fivt.students.dsalnikov.filemap;

import java.io.File;
import java.io.IOException;

public class FileMap {
    private FileMapState state;

    public FileMap(FileMapState startstate) throws IOException {
        state = startstate;
        File f = new File(state.getState());
        if (!f.exists()) f.createNewFile();
        state.build(state.getState());
    }

    public FileMap() {
        state = null;
    }

    public FileMapState setState(FileMapState s) {
        return state = s;
    }

    public FileMapState getState() {
        return state;
    }

    public void deleteiIfEmpty() {
        if (state.isEmpty()) {
            File f = new File(state.getState());
            f.delete();
        }
    }

    public void deletefile() throws IOException {
        File f = new File(state.getState());
        if (!f.exists()) {
            f.createNewFile();
        }
        state.put(state.getState());
    }

}
