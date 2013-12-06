package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.FileReader;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.FileWriter;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalDataTable;

import java.io.IOException;
import java.text.ParseException;

public class DataTable extends UniversalDataTable<String> {

    public DataTable() {
        super();
    }

    public DataTable(String name) {
        super(name);
    }

    @Override
    public String put(String key, String value) {
        return putSimple(key, value);
    }

    @Override
    public int commit() {
        return commitWithoutWriteToDataBase();
    }

    @Override
    public void load() throws IOException, ParseException {
        FileReader fileReader = new FileReader(getWorkingDirectory(), this);
        while (fileReader.checkingLoadingConditions()) {
            fileReader.getNextKey();
        }
        while (fileReader.valuesToReadExists()) {
            fileReader.putValueToTable(valueConverter.convertStringToValueType(fileReader.getNextValue()));
        }
        fileReader.closeResources();
    }

    @Override
    public void writeToDataBase() throws IOException {
        FileWriter fileWriter = new FileWriter();
        fileWriter.writeDataToFile(getWorkingDirectory(), this);
    }

}
