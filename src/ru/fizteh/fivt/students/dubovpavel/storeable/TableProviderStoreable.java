package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.filemap.Serial;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.strings.TableProviderStorageExtended;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TableProviderStoreable<DB extends FileRepresentativeDataBase<Storeable> & Table>
        extends TableProviderStorageExtended<DB> implements TableProvider {
    private TableStoreableBuilder dataBaseBuilder;

    public TableProviderStoreable(Storage storage, TableStoreableBuilder builder) {
        super(storage);
        dataBaseBuilder = builder;
    }

    private ArrayList<Class<?>> collectFields(Table table) {
        ArrayList<Class<?>> result = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            result.add(table.getColumnType(i));
        }
        return result;
    }

    @Override
    public void removeTable(String name) throws IOException {
        super.removeTableExplosive(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (columnTypes == null || columnTypes.size() == 0) {
            throw new IllegalArgumentException();
        }
        for (Class<?> type : columnTypes) {
            if (!TypeNamesMatcher.NAME_BY_CLASS.containsKey(type)) {
                throw new IllegalArgumentException();
            }
        }
        dataBaseBuilder.setFields(new ArrayList<Class<?>>(columnTypes));
        return super.createTableExplosive(name);
    }

    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        StoreableImplTransformer transformer = new StoreableImplTransformer(collectFields(table));
        return transformer.serialize(value);
    }

    public Storeable deserialize(Table table, String value) throws ParseException {
        StoreableImplTransformer transformer = new StoreableImplTransformer(collectFields(table));
        try {
            return transformer.deserialize(value);
        } catch (Serial.SerialException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Storeable createFor(Table table) {
        return new StoreableImpl(collectFields(table));
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values == null) {
            throw new IllegalArgumentException("Value is null");
        }
        StoreableImpl row = new StoreableImpl(collectFields(table));
        if (values.size() != row.size()) {
            throw new IllegalArgumentException("Value size mismatches");
        }
        int i = 0;
        for (Object value : values) {
            row.setColumnAt(i++, value);
        }
        return row;
    }
}
