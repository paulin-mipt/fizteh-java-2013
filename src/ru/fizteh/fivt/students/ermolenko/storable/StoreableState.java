package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class StoreableState {

    private StoreableTableProvider provider;
    private StoreableTable currentTable;

    public StoreableState(File inFile) throws IOException {

        currentTable = null;
        StoreableTableProviderFactory factory = new StoreableTableProviderFactory();
        provider = factory.create(inFile.getPath());
    }

    public StoreableTable getTable(String name) {

        return provider.getTable(name);
    }

    public StoreableTable getCurrentTable() {

        return currentTable;
    }

    public int getChangesBaseSize() {

        return currentTable.getChangesBaseSize();
    }

    public StoreableTableProvider getProvider() {

        return provider;
    }

    public void setCurrentTable(String name, List<Class<?>> columnOfTypes, StoreableTableProvider provider,
                                HashMap<String, Storeable> dataBase, File file) {

        currentTable = provider.getTable(name);
        currentTable.changeCurrentTable(columnOfTypes, provider, dataBase, file);

    }

    public StoreableTable createTable(String name, List<Class<?>> inColumnTypes) throws IOException {

        return provider.createTable(name, inColumnTypes);
    }

    public void deleteTable(String name) throws IOException {

        MultiFileHashMapUtils.deleteDirectory(provider.getTable(name).getDataFile());
        provider.removeTable(name);
        currentTable = null;
    }

    public Storeable putToCurrentTable(String key, Storeable value) {

        return currentTable.put(key, value);
    }

    public Storeable getFromCurrentTable(String key) {

        return currentTable.get(key);
    }

    public Storeable removeFromCurrentTable(String key) {

        return currentTable.remove(key);
    }
}
