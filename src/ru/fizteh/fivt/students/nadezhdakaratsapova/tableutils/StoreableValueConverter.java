package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.text.ParseException;

public class StoreableValueConverter implements ValueConverter<Storeable> {
    private TableProvider tableProvider;
    private Table table;

    public StoreableValueConverter(TableProvider provider, Table table) {
        tableProvider = provider;
        this.table = table;
    }

    public String convertValueTypeToString(Storeable value) {
        return tableProvider.serialize(table, value);
    }

    public Storeable convertStringToValueType(String value) throws ParseException {
        return tableProvider.deserialize(table, value);
    }
}
