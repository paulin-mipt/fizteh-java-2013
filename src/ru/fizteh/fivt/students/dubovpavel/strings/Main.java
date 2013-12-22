package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.executor.Feeder;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerGet;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerHalt;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerPut;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerRemove;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DispatcherMultiFileHashMapBuilder;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.PerformerCreate;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.PerformerDrop;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.PerformerExit;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.PerformerSafeDataBaseOperationWrapper;
import ru.fizteh.fivt.students.dubovpavel.strings.performers.PerformerCommit;
import ru.fizteh.fivt.students.dubovpavel.strings.performers.PerformerRollBack;
import ru.fizteh.fivt.students.dubovpavel.strings.performers.PerformerSize;
import ru.fizteh.fivt.students.dubovpavel.strings.performers.PerformerUse;

public class Main {
    public static void main(String[] args) {
        DispatcherMultiFileHashMapBuilder dispatcherMultiFileHashMapBuilder = new DispatcherMultiFileHashMapBuilder();
        StorageBuilder storageBuilder = new StorageBuilder();
        storageBuilder.setDataBaseBuilder(new StringWrappedMindfulDataBaseMultiFileHashMapBuilder());
        storageBuilder.setPath(true, "fizteh.db.dir");
        dispatcherMultiFileHashMapBuilder.setStorageBuilder(storageBuilder);
        dispatcherMultiFileHashMapBuilder.addPerformer(
                new PerformerSafeDataBaseOperationWrapper(new PerformerGet()));
        dispatcherMultiFileHashMapBuilder.addPerformer(
                new PerformerSafeDataBaseOperationWrapper(new PerformerPut()));
        dispatcherMultiFileHashMapBuilder.addPerformer(
                new PerformerSafeDataBaseOperationWrapper(new PerformerRemove()));
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerCreate());
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerDrop());
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerUse());
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerHalt());
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerExit());
        dispatcherMultiFileHashMapBuilder.addPerformer(new PerformerSafeDataBaseOperationWrapper(new PerformerSize()));
        dispatcherMultiFileHashMapBuilder.addPerformer(
                new PerformerSafeDataBaseOperationWrapper(new PerformerCommit()));
        dispatcherMultiFileHashMapBuilder.addPerformer(
                new PerformerSafeDataBaseOperationWrapper(new PerformerRollBack()));
        Feeder.feed(dispatcherMultiFileHashMapBuilder, args);
    }
}
