package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Executor;

public class StoreableExecutor extends Executor<StoreableState> {

    public StoreableExecutor() {

        list();
    }

    @Override
    public void list() {

        Command use = new CmdUse();
        mapOfCmd.put(use.getName(), use);
        Command create = new CmdCreate();
        mapOfCmd.put(create.getName(), create);
        Command drop = new CmdDrop();
        mapOfCmd.put(drop.getName(), drop);
        Command commit = new CmdCommit();
        mapOfCmd.put(commit.getName(), commit);
        Command size = new CmdSize();
        mapOfCmd.put(size.getName(), size);
        Command rollback = new CmdRollback();
        mapOfCmd.put(rollback.getName(), rollback);

        Command put = new StoreablePut();
        mapOfCmd.put(put.getName(), put);
        Command get = new StoreableGet();
        mapOfCmd.put(get.getName(), get);
        Command remove = new StoreableRemove();
        mapOfCmd.put(remove.getName(), remove);
        Command exit = new StoreableExit();
        mapOfCmd.put(exit.getName(), exit);
    }
}
