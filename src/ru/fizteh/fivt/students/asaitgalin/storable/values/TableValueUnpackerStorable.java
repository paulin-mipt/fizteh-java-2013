package ru.fizteh.fivt.students.asaitgalin.storable.values;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container.TableValueUnpacker;

public class TableValueUnpackerStorable implements TableValueUnpacker<Storeable> {
    private Table table;
    private TableProvider provider;

    public TableValueUnpackerStorable(Table table, TableProvider provider) {
        this.table = table;
        this.provider = provider;
    }

    @Override
    public Storeable getValueFromString(String value) throws Exception {
        return provider.deserialize(table, value);
    }
}
