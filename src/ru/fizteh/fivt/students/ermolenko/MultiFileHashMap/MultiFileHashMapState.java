package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MultiFileHashMapState {

    private MultiFileHashMapTableProvider provider;
    private MultiFileHashMapTable currentTable;

    public MultiFileHashMapState(File inFile) throws IOException {

        currentTable = null;
        MultiFileHashMapTableProviderFactory factory = new MultiFileHashMapTableProviderFactory();
        provider = factory.create(inFile.getPath());
    }

    public int getChangesBaseSize() {

        return currentTable.getChangesBaseSize();
    }

    public MultiFileHashMapTable createTable(String name) throws IOException {

        MultiFileHashMapTable tmp = provider.createTable(name);
        return tmp;
    }

    public MultiFileHashMapTable getTable(String name) throws IOException {

        return provider.getTable(name);
    }

    public MultiFileHashMapTable getCurrentTable() throws IOException {

        return currentTable;
    }

    public void setCurrentTable(String name, Map<String, String> inMap, File inFile) throws IOException {

        currentTable = provider.getTable(name);
        currentTable.changeCurrentTable(inMap, inFile);
    }

    public void deleteTable(String name) throws IOException {

        provider.removeTable(name);
        currentTable = null;
    }

    public String putToCurrentTable(String key, String value) {

        return currentTable.put(key, value);
    }

    public String getFromCurrentTable(String key) {

        return currentTable.get(key);
    }

    public String removeFromCurrentTable(String key) {

        return currentTable.remove(key);
    }
}
