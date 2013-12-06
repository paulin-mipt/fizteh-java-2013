package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.students.dubovpavel.executor.Feeder;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerGet;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerHalt;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerPut;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.PerformerRemove;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.PerformerDrop;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.PerformerExit;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.PerformerSafeDataBaseOperationWrapper;
import ru.fizteh.fivt.students.dubovpavel.storeable.performers.PerformerCreateStoreable;
import ru.fizteh.fivt.students.dubovpavel.strings.performers.PerformerCommit;
import ru.fizteh.fivt.students.dubovpavel.strings.performers.PerformerRollBack;
import ru.fizteh.fivt.students.dubovpavel.strings.performers.PerformerSize;
import ru.fizteh.fivt.students.dubovpavel.strings.performers.PerformerUse;

public class Main {
    public static void main(String[] args) {
        DispatcherStoreableBuilder dispatcherBuilder = new DispatcherStoreableBuilder();
        StorageBuilder storageBuilder = new StorageBuilder();
        TableStoreableBuilder dataBaseBuilder = new TableStoreableBuilder();
        storageBuilder.setDataBaseBuilder(dataBaseBuilder);
        storageBuilder.setPath(true, "fizteh.db.dir");
        dispatcherBuilder.setStorageBuilder(storageBuilder);
        dispatcherBuilder.addPerformer(new PerformerSafeDataBaseOperationWrapper(new PerformerGet()));
        dispatcherBuilder.addPerformer(new PerformerSafeDataBaseOperationWrapper(new PerformerPut()));
        dispatcherBuilder.addPerformer(new PerformerSafeDataBaseOperationWrapper(new PerformerRemove()));
        dispatcherBuilder.addPerformer(new PerformerCreateStoreable(dataBaseBuilder));
        dispatcherBuilder.addPerformer(new PerformerDrop());
        dispatcherBuilder.addPerformer(new PerformerUse());
        dispatcherBuilder.addPerformer(new PerformerHalt());
        dispatcherBuilder.addPerformer(new PerformerExit());
        dispatcherBuilder.addPerformer(new PerformerSafeDataBaseOperationWrapper(new PerformerSize()));
        dispatcherBuilder.addPerformer(new PerformerSafeDataBaseOperationWrapper(new PerformerCommit()));
        dispatcherBuilder.addPerformer(new PerformerSafeDataBaseOperationWrapper(new PerformerRollBack()));
        Feeder.feed(dispatcherBuilder, args);
    }
}
