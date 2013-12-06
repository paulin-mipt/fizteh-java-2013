package ru.fizteh.fivt.students.vyatkina.database.filemap;

import ru.fizteh.fivt.students.vyatkina.database.StringTable;
import ru.fizteh.fivt.students.vyatkina.database.superior.SuperTable;

public class FileMapTable extends SuperTable<String> implements StringTable {

    public FileMapTable(String name) {
        super(name);
    }
}
