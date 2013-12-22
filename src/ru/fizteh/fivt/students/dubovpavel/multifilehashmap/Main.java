package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Feeder;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerGet;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerHalt;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerPut;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerRemove;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.*;

public class Main {
    public static void main(String[] args) {
        DispatcherMultiFileHashMapBuilder dispatcherMultiFileHashMapBuilder = new DispatcherMultiFileHashMapBuilder();
        StorageBuilder storageBuilder = new StorageBuilder();
        storageBuilder.setDataBaseBuilder(new DataBaseMultiFileHashMapBuilder());
        storageBuilder.setPath(true, "fizteh.db.dir");
        dispatcherMultiFileHashMapBuilder.setStorageBuilder(storageBuilder);
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerSafeDataBaseOperationWrapper(new PerformerGet()));
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerSafeDataBaseOperationWrapper(new PerformerPut()));
        dispatcherMultiFileHashMapBuilder.addPerformer(
                new PerformerSafeDataBaseOperationWrapper(new PerformerRemove()));
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerCreate());
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerDrop());
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerUse());
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerHalt());
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerExit());
        Feeder.feed(dispatcherMultiFileHashMapBuilder, args);
    }
}
