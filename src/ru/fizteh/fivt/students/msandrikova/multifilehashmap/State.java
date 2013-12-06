package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.msandrikova.filemap.DatabaseMap;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;
import ru.fizteh.fivt.students.msandrikova.storeable.StoreableTableProviderFactory;

public class State {
    public boolean isMultiFileHashMap;
    public boolean isStoreable;
    public ru.fizteh.fivt.students.msandrikova.multifilehashmap.ChangesCountingTable currentTable;
    public ru.fizteh.fivt.students.msandrikova.multifilehashmap.ChangesCountingTableProvider tableProvider;
    public ru.fizteh.fivt.students.msandrikova.storeable.ChangesCountingTable currentStoreableTable;
    public ru.fizteh.fivt.students.msandrikova.storeable.ChangesCountingTableProvider storeableTableProvider;
    
    public State(boolean isMultiHashFileMap, boolean isStoreable, String dir) {
        this.isMultiFileHashMap = isMultiHashFileMap;
        this.isStoreable = isStoreable;
        if (this.isMultiFileHashMap) {
            this.currentTable = null;
            ru.fizteh.fivt.students.msandrikova.multifilehashmap.ChangesCountingTableProviderFactory 
            factory = new MyTableProviderFactory();
            try { 
                this.tableProvider = factory.create(dir);
            } catch (IllegalArgumentException e) {
                Utils.generateAnError(e.getMessage(), "state", false);
            }
        } else if (this.isStoreable) {
             this.currentStoreableTable = null;
             ru.fizteh.fivt.students.msandrikova.storeable.ChangesCountingTableProviderFactory 
             storeableFactory = new StoreableTableProviderFactory();
                try {
                    this.storeableTableProvider = storeableFactory.create(dir);
                } catch (IllegalArgumentException | IOException e) {
                    Utils.generateAnError(e.getMessage(), "state", false);
                }
             
        } else {
            this.currentTable = new DatabaseMap(new File(dir), "db.dat");
            this.tableProvider = null;
        }
    }
    
    
    
    
}
