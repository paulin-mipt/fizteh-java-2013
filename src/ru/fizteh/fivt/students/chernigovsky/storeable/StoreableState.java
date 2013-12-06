package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;
import java.text.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreableState implements State {
    private ExtendedStoreableTable currentTable;
    private ExtendedStoreableTableProvider currentTableProvider;

    public StoreableState(ExtendedStoreableTable newTable, ExtendedStoreableTableProvider newTableProvider) {
        currentTable = newTable;
        currentTableProvider = newTableProvider;
    }

    public ExtendedStoreableTable getCurrentTable() {
        return currentTable;
    }

    public void changeCurrentTable(String name) {
        currentTable = currentTableProvider.getTable(name);
    }

    public ExtendedStoreableTableProvider getCurrentTableProvider() {
        return currentTableProvider;
    }

    public boolean currentTableIsNull() {
        if (currentTable == null) {
            return true;
        }
        return false;
    }

    public String getFromCurrentTable(String key) {
        Storeable value = currentTable.get(key);
        if (value == null) {
            return null;
        }
        return currentTableProvider.serialize(currentTable, currentTable.get(key));
    }

    public String putToCurrentTable(String key, String value) throws ParseException {
        Storeable deserializedValue = currentTableProvider.deserialize(currentTable, value);
        Storeable oldValue = currentTable.put(key, deserializedValue);
        if (oldValue == null) {
            return null;
        }
        return currentTableProvider.serialize(currentTable, oldValue);
    }

    public String removeFromCurrentTable(String key) {
        Storeable value = currentTable.remove(key);
        if (value == null) {
            return null;
        }
        return currentTableProvider.serialize(currentTable, value);
    }

    public boolean removeTable(String name) throws IOException{
        try {
            currentTableProvider.removeTable(name);
        } catch (IllegalStateException ex) {
            return false;
        }
        return true;
    }

    public void checkDropTableUsing(String name) {
        if (currentTableProvider.getTable(name) == currentTable) {
            currentTable = null;
        }
    }

    public boolean createTable(String name) {
        return false;
    }

    public boolean createStoreableTable(String name, String types) throws IOException {
        types = types.substring(1, types.length() - 1);
        String[] listOfTypes = types.trim().split("\\s+");
        List<Class<?>> listOfClasses = new ArrayList<Class<?>>();
        for (String string : listOfTypes) {
            if (TypeEnum.getBySignature(string) == null) {
                throw new IOException("wrong type ()");
            }
            listOfClasses.add(TypeEnum.getBySignature(string).getClazz());
        }
        if (currentTableProvider.createTable(name, listOfClasses) == null) {
            return true;
        }
        return false;
    }

    public boolean isTableExists(String name) {
        return currentTableProvider.getTable(name) != null;
    }

    public int getDiffCount() {
        return currentTable.getDiffCount();
    }

    public void writeTable() throws IOException {
        StoreableUtils.writeTable(currentTable, currentTableProvider);
    }

    public void readTable() throws IOException {
        StoreableUtils.readTable(currentTable, currentTableProvider);
    }

    public boolean isCurrentTableProviderNull() {
        return currentTableProvider == null;
    }

    public int commit() throws IOException {
        return currentTable.commit();
    }

    public int rollback() {
        return currentTable.rollback();
    }

    public int size() {
        return currentTable.size();
    }
}
