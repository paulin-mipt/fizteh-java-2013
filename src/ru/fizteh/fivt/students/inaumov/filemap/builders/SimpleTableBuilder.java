package ru.fizteh.fivt.students.inaumov.filemap.builders;

import ru.fizteh.fivt.students.inaumov.filemap.base.StringDatabaseTable;

import java.io.File;
import java.util.Set;

public class SimpleTableBuilder implements TableBuilder {
    StringDatabaseTable table;

    public SimpleTableBuilder(StringDatabaseTable table) {
        this.table = table;
    }

    @Override
    public String get(String key) {
        return table.rawGet(key);
    }

    @Override
    public void put(String key, String value) {
        table.rawPut(key, value);
    }

    @Override
    public Set<String> getKeys() {
        return table.keyValueHashMap.keySet();
    }

    @Override
    public File getTableDir() {
        return new File(table.getDir(), table.getName());
    }

    @Override
    public void setCurrentFile(File currentFile) {
        //
    }
}
