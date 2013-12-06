package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 10.11.13
 * Time: 13:19
 * To change this template use File | Settings | File Templates.
 */
public class GlobalFileMapState {
    private Table tableStrings = null;
    private ru.fizteh.fivt.storage.structured.Table tableStoreable = null;
    private TableProvider tableProviderStrings = null;
    private ru.fizteh.fivt.storage.structured.TableProvider tableProviderStoreable = null;
    private boolean isStoreableMode = false;

    public GlobalFileMapState() {
    }

    public GlobalFileMapState(Table table, TableProvider tableProvider) {
        tableStrings = table;
        tableProviderStrings = tableProvider;
    }

    public GlobalFileMapState(ru.fizteh.fivt.storage.structured.Table table,
                              ru.fizteh.fivt.storage.structured.TableProvider tableProvider) {
        tableStoreable = table;
        tableProviderStoreable = tableProvider;
        isStoreableMode = true;
    }

    public boolean isValidTable() {
        if (isStoreableMode) {
            if (tableStoreable == null) {
                return false;
            }
        } else {
            if (tableStrings == null) {
                return false;
            }
        }
        return true;
    }

    public void changeTable(Table table) throws IllegalArgumentException {
        if (isStoreableMode) {
            throw new IllegalArgumentException("GFMS: changeTable: Storeable mode is on!");
        }
        tableStrings = table;
    }

    public void changeTable(ru.fizteh.fivt.storage.structured.Table table) {
        if (!isStoreableMode) {
            throw new IllegalArgumentException("GFMS: changeTable: Storeable mode is off!");
        }
        tableStoreable = table;
    }

    public void commit() throws IOException {
        if (isStoreableMode) {
            tableStoreable.commit();
        }
    }

    public void createTable(String name) throws IllegalArgumentException {
        if (isStoreableMode) {
            throw new IllegalArgumentException("GFMS: createTable! Storeable mode is on! "
                    + "This command can't work in this mode!");
        } else {
            tableProviderStrings.createTable(name);
        }
    }

    public void removeTable(String name) throws IOException {
        if (isStoreableMode) {
            tableProviderStoreable.removeTable(name);
        } else {
            tableProviderStrings.removeTable(name);
        }
    }

    public void saveTable() throws IOException {
        if (isStoreableMode) {
            tableStoreable.commit();
        } else {
            tableStrings.commit();
        }
    }

    public void get(String key) {
        if (isStoreableMode) {
            tableStoreable.get(key);
        } else {
            tableStrings.get(key);
        }
    }

    public void put(String key, String value) throws IOException {
        if (!isStoreableMode) {
            tableStrings.put(key, value);
        }
    }

    public void removeKey(String key) {
        if (isStoreableMode) {
            tableStoreable.remove(key);
        } else {
            tableStrings.remove(key);
        }
    }

    public void rollback() {
        if (isStoreableMode) {
            tableStoreable.rollback();
        } else {
            tableStrings.rollback();
        }
    }

    public void size() {
        if (isStoreableMode) {
            tableStoreable.size();
        } else {
            tableStrings.size();
        }
    }

    public void use(String name) throws IOException {
        throw new IOException("GFMS: use: Sorry, this method was depritiated!:(");
    }
}
