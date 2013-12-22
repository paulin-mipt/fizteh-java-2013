package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.filemap.DataBase;
import ru.fizteh.fivt.students.dubovpavel.filemap.Serial;

import java.io.File;

public abstract class FileRepresentativeDataBase<V> extends DataBase<V> {
    public FileRepresentativeDataBase(Serial<V> builder) {
        super(builder);
    }

    public abstract File getPath();
}
