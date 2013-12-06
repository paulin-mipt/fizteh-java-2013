package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.filemap.StringSerial;

public class DataBaseMultiFileHashMapBuilder extends DataBaseBuilder<FileRepresentativeDataBase> {
    public DataBaseMultiFileHashMap construct() {
        assert (dir != null);
        return new DataBaseMultiFileHashMap(dir, new StringSerial());
    }
}
