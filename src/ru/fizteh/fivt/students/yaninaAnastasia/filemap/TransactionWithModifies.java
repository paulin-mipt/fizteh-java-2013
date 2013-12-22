package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.HashMap;
import java.util.HashSet;

public class TransactionWithModifies {
    HashMap<String, Storeable> modifiedData;
    HashSet<String> deletedKeys;
    DatabaseTable tableInstance;
    int uncommittedChanges;

    public TransactionWithModifies() {
        this.modifiedData = new HashMap<String, Storeable>();
        this.deletedKeys = new HashSet<String>();
        this.uncommittedChanges = 0;
    }

    public void defineStorage(DatabaseTable newInstance) {
        this.tableInstance = newInstance;
    }
}
