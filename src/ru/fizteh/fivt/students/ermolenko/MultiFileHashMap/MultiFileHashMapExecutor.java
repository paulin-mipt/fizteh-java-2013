package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Executor;

public class MultiFileHashMapExecutor extends Executor<MultiFileHashMapState> {

    public MultiFileHashMapExecutor() {

        list();
    }

    @Override
    public void list() {

        Command create = new CmdCreate();
        mapOfCmd.put(create.getName(), create);
        Command drop = new CmdDrop();
        mapOfCmd.put(drop.getName(), drop);
        Command use = new CmdUse();
        mapOfCmd.put(use.getName(), use);

        Command commit = new CmdCommit();
        mapOfCmd.put(commit.getName(), commit);
        Command rollback = new CmdRollback();
        mapOfCmd.put(rollback.getName(), rollback);
        Command size = new CmdSize();
        mapOfCmd.put(size.getName(), size);

        Command exit = new MultiFileHashMapExit();
        mapOfCmd.put(exit.getName(), exit);
        Command get = new MultiFileHashMapGet();
        mapOfCmd.put(get.getName(), get);
        Command put = new MultiFileHashMapPut();
        mapOfCmd.put(put.getName(), put);
        Command remove = new MultiFileHashMapRemove();
        mapOfCmd.put(remove.getName(), remove);
    }
}
