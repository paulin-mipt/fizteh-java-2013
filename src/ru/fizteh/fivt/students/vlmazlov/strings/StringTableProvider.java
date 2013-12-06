package ru.fizteh.fivt.students.vlmazlov.strings;

import ru.fizteh.fivt.students.vlmazlov.generics.GenericTableProvider;
import ru.fizteh.fivt.students.vlmazlov.utils.ProviderReader;
import ru.fizteh.fivt.students.vlmazlov.utils.ProviderWriter;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityChecker;

import java.io.File;
import java.io.IOException;


public class StringTableProvider extends GenericTableProvider<String, StringTable> 
implements DiffCountingTableProvider {

    public StringTableProvider(String root, boolean autoCommit) throws ValidityCheckFailedException {
        super(root, autoCommit);
    }

    @Override
    protected StringTable instantiateTable(String name, Object[] args) {
        return new StringTable(this, name, autoCommit);
    }

    public StringTable createTable(String name) {
        return super.createTable(name, null);
    }

    @Override
    public String deserialize(StringTable table, String value) {
        return new String(value);
    }

    @Override
    public String serialize(StringTable table, String value) {
        return new String(value);
    }

    @Override
    public void read() throws IOException, ValidityCheckFailedException {
        for (File file : ProviderReader.getTableDirList(this)) {

            ValidityChecker.checkMultiTableRoot(file);

            StringTable table = createTable(file.getName());
            ProviderReader.readMultiTable(file, table, this);
            //read data has to be preserved
            table.commit();
        }
    }

    @Override
    public void write() throws IOException, ValidityCheckFailedException {
        ProviderWriter.writeProvider(this);
    }
}
