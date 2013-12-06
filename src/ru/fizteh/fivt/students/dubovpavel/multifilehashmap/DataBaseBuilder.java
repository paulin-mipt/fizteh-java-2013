package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;

import java.io.File;

public abstract class DataBaseBuilder<DB extends DataBaseHandler> {
    protected File dir = null;

    public void setPath(File folder) {
        dir = folder;
    }

    public abstract DB construct();
}
