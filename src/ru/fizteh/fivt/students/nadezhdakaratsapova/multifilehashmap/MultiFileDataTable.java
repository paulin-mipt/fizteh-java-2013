package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.StringValueConverter;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class MultiFileDataTable extends UniversalDataTable<String> implements Table {

    public MultiFileDataTable() {
        super();
    }

    public MultiFileDataTable(String name) {
        super(name);
    }

    public MultiFileDataTable(String name, File dir) {
        super(name, dir, new StringValueConverter());
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        if ((key == null) || (key.trim().isEmpty()) || (value == null) || (value.trim().isEmpty())) {
            throw new IllegalArgumentException("Not correct key or value");
        }
        return putSimple(key, value);
    }

    @Override
    public int commit() {
        return commitWithoutWriteToDataBase();
    }

    @Override
    public void load() throws IOException, IllegalArgumentException, ParseException {
        universalLoad();
    }

    @Override
    public void writeToDataBase() throws IOException {
        writeToDataBaseWithoutSignature();
    }
}

