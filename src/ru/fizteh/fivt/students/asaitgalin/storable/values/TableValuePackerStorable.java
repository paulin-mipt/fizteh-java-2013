package ru.fizteh.fivt.students.asaitgalin.storable.values;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container.TableValuePacker;

public class TableValuePackerStorable implements TableValuePacker<Storeable> {
    private Table table;
    private TableProvider provider;

    public TableValuePackerStorable(Table table, TableProvider provider) {
        this.table = table;
        this.provider = provider;
    }

    @Override
    public String getValueString(Storeable storeable) throws Exception {
        return provider.serialize(table, storeable);
    }

}
