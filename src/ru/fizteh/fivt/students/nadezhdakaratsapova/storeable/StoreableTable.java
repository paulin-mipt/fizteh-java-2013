package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.SignatureController;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.StoreableValueConverter;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTable extends UniversalDataTable<Storeable> implements Table {

    private StoreableTableProvider tableProvider;
    private List<Class<?>> columnTypes = new ArrayList<Class<?>>();


    public StoreableTable(String name, File dir, List<Class<?>> types, StoreableTableProvider provider) {
        tableProvider = provider;
        valueConverter = new StoreableValueConverter(tableProvider, this);
        dataBaseDirectory = dir;
        tableName = name;
        columnTypes = types;
    }

    public StoreableTable(StoreableTable table) throws IOException, ParseException {
        tableProvider = table.tableProvider;
        valueConverter = new StoreableValueConverter(tableProvider, this);
        dataBaseDirectory = table.dataBaseDirectory;
        tableName = table.tableName;
        columnTypes = table.columnTypes;
        this.load();
    }

    @Override
    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        if ((key == null) || (key.trim().isEmpty()) || (value == null) || (key.matches("(.*\\s+.*)+"))) {
            throw new IllegalArgumentException("pot correct key or value");
        }
        int signColumnsCount = getColumnsCount();
        int i;
        for (i = 0; i < signColumnsCount; ++i) {
            try {
                SignatureController signatureController = new SignatureController();
                signatureController.checkValueForTable(i, this, value);
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException(e.getMessage());
            }
        }
        try {
            value.getColumnAt(i);
        } catch (IndexOutOfBoundsException e) {
            return putSimple(key, value);
        }
        throw new ColumnFormatException("put: invalid value: invalid storeable type");


    }


    public int getColumnsCount() {
        checkNotClosed();
        return columnTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkNotClosed();
        if (columnIndex >= columnTypes.size() || columnIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }

    @Override
    public int commit() throws IOException {
        checkNotClosed();
        int commitSize = 0;
        tableChangesLock.writeLock().lock();
        try {
            checkNotClosed();
            commitSize = commitWithoutWriteToDataBase();
            writeToDataBase();
            return commitSize;
        } finally {
            tableChangesLock.writeLock().unlock();
        }

    }

    @Override
    public void load() throws IOException, ParseException {
        universalLoad();
    }

    @Override
    public void writeToDataBase() throws IOException {
        writeToDataBaseWithoutSignature();
        File sign = new File(new File(getWorkingDirectory(), getName()), "signature.tsv");
        sign.createNewFile();
        SignatureController signatureController = new SignatureController();
        signatureController.writeSignatureToFile(sign, columnTypes);
    }
}
