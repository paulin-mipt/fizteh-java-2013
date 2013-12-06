package ru.fizteh.fivt.students.dubovpavel.filemap;

import ru.fizteh.fivt.students.dubovpavel.executor.Feeder;
import ru.fizteh.fivt.students.dubovpavel.filemap.performers.*;

public class Main {
    public static void main(String[] args) {
        DispatcherFileMapBuilder dispatcherFileMapBuilder = new DispatcherFileMapBuilder();
        dispatcherFileMapBuilder.setRepoPath("fizteh.db.dir");
        dispatcherFileMapBuilder.addPerformer(new PerformerExit());
        dispatcherFileMapBuilder.addPerformer(new PerformerGet());
        dispatcherFileMapBuilder.addPerformer(new PerformerHalt());
        dispatcherFileMapBuilder.addPerformer(new PerformerPut());
        dispatcherFileMapBuilder.addPerformer(new PerformerRemove());
        Feeder.feed(dispatcherFileMapBuilder, args);
    }
}
