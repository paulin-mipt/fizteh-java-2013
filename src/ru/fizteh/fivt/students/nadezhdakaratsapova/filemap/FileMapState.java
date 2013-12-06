package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalTableProvider;

import java.io.File;
import java.io.IOException;


public class FileMapState implements UniversalTableProvider {

    public DataTable dataStorage;

    private File dataFile;

    public FileMapState(File file) {
        dataFile = file;
        dataStorage = new DataTable(file.getName());
    }

    public File getDataFile() {
        return dataFile;
    }

    public UniversalDataTable getCurTable() {
        return dataStorage;
    }

    public UniversalDataTable getTable(String name) {
        throw new UnsupportedOperationException("the operation is not supported");
    }

    public void removeTable(String name) throws IOException {
        throw new UnsupportedOperationException("the operation is not supported");
    }

    public UniversalDataTable setCurTable(String newTable) throws IOException {
        throw new UnsupportedOperationException("the operation is not supported");
    }

}
